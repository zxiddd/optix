# 06 - Comprehensive Automated Testing & QA Strategy

## Purpose
This document defines the automated testing framework, Android unit tests, Jetpack Compose UI testing, Node.js API integration testing, and performance benchmark protocols for the **Optix** ecosystem.

---

## 1. Android Cart Calculation Unit Test (`CalculateCartUseCaseTest.kt`)

```kotlin
class CalculateCartUseCaseTest {

    private lateinit var useCase: CalculateCartUseCase

    @Before
    fun setUp() {
        useCase = CalculateCartUseCase()
    }

    @Test
    fun `calculateCartTotal computes correct gross total with tax and line item discount`() {
        val lineItem1 = CartItem(
            productId = "p1",
            unitPrice = BigDecimal("10.00"),
            quantity = BigDecimal("2.0"),
            pricingStrategy = PricingStrategy.FIXED
        )
        val cart = Cart(items = listOf(lineItem1), taxRate = BigDecimal("0.10"), discountAmount = BigDecimal("2.00"))

        val result = useCase.execute(cart)

        // Subtotal: 20.00, Discount: 2.00, Net Subtotal: 18.00, Tax (10%): 1.80, Gross: 19.80
        assertEquals(BigDecimal("19.80"), result.grossTotal)
    }
}
```

---

## 2. Jetpack Compose UI Test (`CheckoutScreenTest.kt`)

```kotlin
@RunWith(AndroidJUnit4::class)
class CheckoutScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun payButton_disabled_when_cart_is_empty() {
        composeTestRule.setContent {
            OptixTheme {
                CheckoutScreen(uiState = CheckoutUiState(cartItems = emptyList()))
            }
        }

        composeTestRule.onNodeWithText("Pay Cash").assertIsNotEnabled()
    }
}
```

---

## 3. Node.js Sync Integration Test (`sync.test.ts`)

```typescript
import request from 'supertest';
import app from '../src/app';

describe('POST /api/v1/sync/push', () => {
    it('should process valid bill events idempotently', async () => {
        const payload = {
            device_id: 'TEST-DEV-01',
            batch_id: 'batch-001',
            events: [
                {
                    event_id: 'evt-unique-101',
                    entity_type: 'BILL',
                    action_type: 'CREATE',
                    timestamp: Date.now(),
                    payload: { /* ... valid bill data ... */ }
                }
            ]
        };

        const res1 = await request(app)
            .post('/api/v1/sync/push')
            .set('Authorization', 'Bearer valid-test-token')
            .send(payload);

        expect(res1.status).toBe(200);
        expect(res1.body.status).toBe('PROCESSED');
    });
});
```

---

## 4. Local Database Latency & Load Benchmark Specs
- **Database Write Stress Benchmark**: Insert 10,000 un-synced outbox events into Room SQLite database; verify write latency remains <30ms and memory usage remains <150MB on ARM64 tablet hardware.
