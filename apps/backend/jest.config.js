/** @type {import('ts-jest').JestConfigWithTsJest} */
export default {
  preset: 'ts-jest',
  testEnvironment: 'node',
  roots: ['<rootDir>/src'],
  testMatch: ['**/*.test.ts'],
  moduleNameMapper: {
    '^@optix/escpos-sdk$': '<rootDir>/../../packages/escpos-sdk/src/index.ts',
    '^@optix/shared-types$': '<rootDir>/../../packages/shared-types/src/index.ts',
    '^(\\.{1,2}/.*)\\.js$': '$1'
  },
  transform: {
    '^.+\\.tsx?$': 'ts-jest'
  }
};
