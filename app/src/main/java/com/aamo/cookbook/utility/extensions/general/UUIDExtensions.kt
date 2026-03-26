package com.aamo.cookbook.utility.extensions.general

import java.util.UUID

fun getNewUUID(used: Iterable<UUID>): UUID {
  UUID.randomUUID().let {
    var id = it
    while (used.contains(id)) {
      id = UUID.randomUUID()
    }
    return id
  }
}