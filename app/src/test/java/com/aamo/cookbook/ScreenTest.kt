package com.aamo.cookbook

import org.junit.Test
import java.util.UUID

class ScreenTest {
  @Test
  fun getRoute() {
    val screen = Screen.Recipe
    val route = screen.getRoute()

    assert(route.contains("{${screen.argumentName}}"))
  }

  @Test
  fun getRouteWithArgument() {
    val screen = Screen.Recipe
    val arg = UUID.randomUUID()
    val routeWithArg = screen.getRouteWithArgument(arg.toString())

    assert(routeWithArg.contains("/$arg"))
  }
}