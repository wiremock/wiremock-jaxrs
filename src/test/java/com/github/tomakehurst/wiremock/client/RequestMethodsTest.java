package com.github.tomakehurst.wiremock.client;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;

public class RequestMethodsTest {

  static interface AllRequestMethodsResouce {
    @GET
    void m1();

    @POST
    void m2();

    @PUT
    void m3();

    @DELETE
    void m4();

    @PATCH
    void m5();

    @OPTIONS
    void m6();

    @HEAD
    void m7();
  }

  @Test
  public void testThatRequestMethodsCanBeFound() {
    final List<String> reqMethodsNotFound =
        Arrays.asList(RequestMethod.values()).stream()
            .map((it) -> it.getName())
            .collect(Collectors.toList());
    reqMethodsNotFound.removeAll(Arrays.asList("TRACE", "ANY", "GET_OR_HEAD"));
    final JaxrsInvocationHandler sut = new JaxrsInvocationHandler();
    for (final Method method : AllRequestMethodsResouce.class.getMethods()) {
      final String foundReqMethod = sut.getRequestMethod(method).getName();
      reqMethodsNotFound.remove(foundReqMethod);
    }

    if (!reqMethodsNotFound.isEmpty()) {
      final String msg = String.join("\n", reqMethodsNotFound);
      throw new RuntimeException("Not mapped:\n" + msg);
    }
  }
}
