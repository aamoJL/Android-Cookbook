package com.aamo.cookbook.utility
fun <E: Any> MutableList<E>.swap(indexA: Int, indexB: Int) {
  this[indexA] = this[indexB].also { this[indexB] = this[indexA] }
}