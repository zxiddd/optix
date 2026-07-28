# 03 - ESC/POS Bytecode Protocol & Hardware Driver Implementation

## Purpose
This document provides exact byte code tables, hexadecimal sequences, monochrome raster bitmap conversion algorithms, and Bluetooth/USB socket code blueprints for the **Optix ESC/POS Thermal Printing Engine**.

---

## 1. ESC/POS Hexadecimal Command Reference Table

```
+---------------------------+-----------------------------------+---------------------------------------+
| Command Name              | Hex Bytes                         | ASCII Representation                  |
+---------------------------+-----------------------------------+---------------------------------------+
| Hardware Initialize       | 0x1B, 0x40                        | ESC @                                 |
| Align Left                | 0x1B, 0x61, 0x00                  | ESC a 0                               |
| Align Center              | 0x1B, 0x61, 0x01                  | ESC a 1                               |
| Align Right               | 0x1B, 0x61, 0x02                  | ESC a 2                               |
| Bold Font ON              | 0x1B, 0x45, 0x01                  | ESC E 1                               |
| Bold Font OFF             | 0x1B, 0x45, 0x00                  | ESC E 0                               |
| Double Height / Width ON  | 0x1D, 0x21, 0x11                  | GS ! 17                               |
| Double Height / Width OFF | 0x1D, 0x21, 0x00                  | GS ! 0                                |
| Paper Cut (Partial)       | 0x1D, 0x56, 0x41, 0x00            | GS V A 0                              |
| Cash Drawer Pulse (Pin 2) | 0x1B, 0x70, 0x00, 0x19, 0xFA      | ESC p 0 25 250                        |
+---------------------------+-----------------------------------+---------------------------------------+
```

---

## 2. Monochrome Raster Bitmap Converter Algorithm (1-Bit Monochrome)

```kotlin
// Android Bitmap to ESC/POS Raster Buffer Converter (GS v 0)
fun convertBitmapToEscPosRaster(bitmap: Bitmap): ByteArray {
    val width = bitmap.width
    val height = bitmap.height
    val bytesPerWidth = (width + 7) / 8
    val stream = ByteArrayOutputStream()

    // GS v 0 m xL xH yL yH
    stream.write(byteArrayOf(0x1D, 0x76, 0x30, 0x00))
    stream.write(bytesPerWidth and 0xFF)
    stream.write((bytesPerWidth shr 8) and 0xFF)
    stream.write(height and 0xFF)
    stream.write((height shr 8) and 0xFF)

    for (y in 0 until height) {
        for (x in 0 until bytesPerWidth) {
            var byteVal = 0
            for (bit in 0 until 8) {
                val pixelX = x * 8 + bit
                if (pixelX < width) {
                    val pixel = bitmap.getPixel(pixelX, y)
                    val luminance = (Color.red(pixel) * 0.299 + Color.green(pixel) * 0.587 + Color.blue(pixel) * 0.114)
                    if (luminance < 128) {
                        byteVal = byteVal or (0x80 shr bit)
                    }
                }
            }
            stream.write(byteVal)
        }
    }
    return stream.toByteArray()
}
```

---

## 3. Bluetooth SPP Thermal Socket Writer Implementation

```kotlin
// Native Kotlin Bluetooth SPP Socket Dispatcher
suspend fun sendToBluetoothPrinter(macAddress: String, payload: ByteArray): Boolean = withContext(Dispatchers.IO) {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return@withContext false
    val device = bluetoothAdapter.getRemoteDevice(macAddress)
    val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Standard SPP UUID

    var socket: BluetoothSocket? = null
    try {
        socket = device.createRfcommSocketToServiceRecord(uuid)
        socket.connect()
        val outputStream = socket.outputStream
        outputStream.write(payload)
        outputStream.flush()
        socket.close()
        true
    } catch (e: Exception) {
        socket?.close()
        false
    }
}
```
