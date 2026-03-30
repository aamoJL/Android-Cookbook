package com.aamo.cookbook.utility.extensions.general

import java.util.UUID

fun getUniqueUUID(used: Iterable<UUID>): UUID {
  UUID.randomUUID().let {
    var id = it
    while (used.contains(id)) {
      id = UUID.randomUUID()
    }
    return id
  }
}

fun getUniqueUUIDs(count: Int, used: Iterable<UUID> = emptyList()): Iterable<UUID> {
  val ids = mutableListOf<UUID>()
  val used = used.toMutableList()
  repeat(count) {
    ids.add(getUniqueUUID(used = ids).also {
      used.add(it)
    })
  }
  return ids
}