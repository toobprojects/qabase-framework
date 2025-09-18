package com.toob.qabase.rest.assertions

import io.restassured.response.Response


fun expect(response: Response): RestExpect = RestExpect(response)