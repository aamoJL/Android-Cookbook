package com.aamo.cookbook.test_utility.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first

suspend fun <T> Flow<T>.load(): T {
  return this.drop(1).first()
}