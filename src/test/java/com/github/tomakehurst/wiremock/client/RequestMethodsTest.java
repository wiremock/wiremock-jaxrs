package com.github.tomakehurst.wiremock.client;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
        Arrays.asList(RequestMethod.values())
            .stream()
            .map((it) -> it.getName())
            .collect(Collectors.toList());
    reqMethodsNotFound.removeAll(Arrays.asList("TRACE", "ANY"));
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
