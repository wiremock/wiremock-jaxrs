package com.github.tomakehurst.wiremock.client;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class JaxrsInvocationHandler implements InvocationHandler {

  private RequestMethod requestMethod;
  private Class<?> returnType;
  private String path;
  private List<InvocationParam> queryParams;
  private List<String> requestContentTypeList;
  private List<String> responseContentTypeList;
  private Object postObject;

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args)
      throws Throwable {
    this.requestMethod = this.getRequestMethod(method);

    this.returnType = method.getReturnType();
    this.queryParams = this.createQueryParams(method.getParameters(), args);
    final List<InvocationParam> pathParams = this.createPathParams(method.getParameters(), args);
    this.path = this.substituteParams(this.createPath(method), pathParams);
    this.requestContentTypeList = this.getRequestContentTypeList(method);
    this.responseContentTypeList = this.getResponseContentTypeList(method);
    this.postObject = this.findPostObject(method.getParameters(), args).orElse(null);
    return null;
  }

  public RequestMethod getRequestMethod() {
    return this.requestMethod;
  }

  public String getPath() {
    return this.path;
  }

  public List<String> getRequestContentTypeList() {
    return this.requestContentTypeList;
  }

  public List<String> getResponseContentTypeList() {
    return this.responseContentTypeList;
  }

  public Class<?> getReturnType() {
    return this.returnType;
  }

  public List<InvocationParam> getQueryParams() {
    return this.queryParams;
  }

  public Optional<Object> findPostObject() {
    return Optional.ofNullable(this.postObject);
  }

  RequestMethod getRequestMethod(final Method method) {
    if (this.findAnnotation(method, GET.class).isPresent()) {
      return RequestMethod.GET;
    } else if (this.findAnnotation(method, POST.class).isPresent()) {
      return RequestMethod.POST;
    } else if (this.findAnnotation(method, PUT.class).isPresent()) {
      return RequestMethod.PUT;
    } else if (this.findAnnotation(method, DELETE.class).isPresent()) {
      return RequestMethod.DELETE;
    } else if (this.findAnnotation(method, PATCH.class).isPresent()) {
      return RequestMethod.PATCH;
    } else if (this.findAnnotation(method, HEAD.class).isPresent()) {
      return RequestMethod.HEAD;
    } else if (this.findAnnotation(method, OPTIONS.class).isPresent()) {
      return RequestMethod.OPTIONS;
    }
    throw new RuntimeException("Cannot find request method of " + method.getName());
  }

  private List<String> getRequestContentTypeList(final Method method) {
    final List<String> list = new ArrayList<>();
    for (final Consumes consumes : method.getAnnotationsByType(Consumes.class)) {
      for (final String value : consumes.value()) {
        list.add(value);
      }
    }
    return list;
  }

  private List<String> getResponseContentTypeList(final Method method) {
    final List<String> list = new ArrayList<>();
    for (final Produces produces : method.getAnnotationsByType(Produces.class)) {
      for (final String value : produces.value()) {
        list.add(value);
      }
    }
    return list;
  }

  private String createPath(final Method method) {
    final Optional<Path> classPath =
        this.findAnnotation(method.getDeclaringClass().getAnnotations(), Path.class);
    final Optional<Path> methodPath = this.findAnnotation(method, Path.class);
    String str = "";
    if (classPath.isPresent()) {
      str += classPath.get().value();
    }
    if (methodPath.isPresent()) {
      str += "/" + methodPath.get().value();
    }
    while (str.contains("//")) {
      str = str.replace("//", "/");
    }
    return str;
  }

  private <T> Optional<T> findAnnotation(final Method method, final Class<T> findAnnotation) {
    final Annotation[] methodAnnotations = method.getAnnotations();
    return this.findAnnotation(methodAnnotations, findAnnotation);
  }

  @SuppressWarnings("unchecked")
  private <T> Optional<T> findAnnotation(
      final Annotation[] methodAnnotations, final Class<T> annotations) {
    for (final Annotation annotation : methodAnnotations) {
      if (annotation.annotationType() == annotations) {
        return Optional.of((T) annotation);
      }
    }
    return Optional.empty();
  }

  private List<InvocationParam> createQueryParams(
      final Parameter[] parameters, final Object... args) {
    final List<InvocationParam> queryParams = new ArrayList<>();
    if (parameters == null || args == null) {
      return queryParams;
    }
    if (parameters.length != args.length) {
      throw new RuntimeException(parameters.length + " != " + args.length);
    }
    for (int i = 0; i < args.length; i++) {
      final Object arg = args[i];
      final Parameter param = parameters[i];
      final Optional<QueryParam> paramAnnotation =
          this.findAnnotation(param.getAnnotations(), QueryParam.class);
      if (paramAnnotation.isPresent()) {
        final String name = paramAnnotation.get().value();
        final InvocationParam invocationParam = new InvocationParam(name, arg);
        queryParams.add(invocationParam);
      }
    }
    return queryParams;
  }

  private Optional<Object> findPostObject(final Parameter[] parameters, final Object... args) {
    if (parameters == null || args == null) {
      return Optional.empty();
    }
    if (parameters.length != args.length) {
      throw new RuntimeException(parameters.length + " != " + args.length);
    }
    for (int i = 0; i < args.length; i++) {
      final Object arg = args[i];
      final Parameter param = parameters[i];
      if ( //
      !this.findAnnotation(param.getAnnotations(), PathParam.class).isPresent() //
          && !this.findAnnotation(param.getAnnotations(), QueryParam.class).isPresent()) {
        return Optional.ofNullable(arg);
      }
    }
    return Optional.empty();
  }

  private List<InvocationParam> createPathParams(
      final Parameter[] parameters, final Object... args) {
    final List<InvocationParam> pathParams = new ArrayList<>();
    if (parameters == null || args == null) {
      return pathParams;
    }
    if (parameters.length != args.length) {
      throw new RuntimeException(parameters.length + " != " + args.length);
    }
    for (int i = 0; i < args.length; i++) {
      final Object arg = args[i];
      final Parameter param = parameters[i];
      final Optional<PathParam> paramAnnotation =
          this.findAnnotation(param.getAnnotations(), PathParam.class);
      if (paramAnnotation.isPresent()) {
        final String name = paramAnnotation.get().value();
        final InvocationParam invocationParam = new InvocationParam(name, arg);
        pathParams.add(invocationParam);
      }
    }
    return pathParams;
  }

  private String substituteParams(final String createPath, final List<InvocationParam> pathParams) {
    String substituted = createPath;
    for (final InvocationParam pathParam : pathParams) {
      if (pathParam.getValue() == null) {
        throw new RuntimeException("Cannot use null value for " + pathParam.getName());
      }
      substituted =
          substituted.replace("{" + pathParam.getName() + "}", pathParam.getValue().toString());
    }
    return substituted;
  }
}
