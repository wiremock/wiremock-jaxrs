package com.github.tomakehurst.wiremock.client;

import com.github.tomakehurst.wiremock.http.RequestMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

class JaxrsInvocationHandler implements InvocationHandler {

  private RequestMethod requestMethod;
  private Class<?> returnType;
  private String path;
  private List<InvocationParam> queryParams;
  private List<InvocationParam> pathParams;
  private List<String> requestContentTypeList;
  private List<String> responseContentTypeList;
  private Object postObject;

  @Override
  public Object invoke(final Object proxy, final Method method, final Object[] args)
      throws Throwable {
    this.requestMethod = getRequestMethod(method);

    this.returnType = method.getReturnType();
    this.queryParams = createQueryParams(method.getParameters(), args);
    this.pathParams = createPathParams(method.getParameters(), args);
    this.path = substituteParams(createPath(method), queryParams, pathParams);
    this.requestContentTypeList = getRequestContentTypeList(method);
    this.responseContentTypeList = getResponseContentTypeList(method);
    this.postObject = findPostObject(method.getParameters(), args).orElse(null);
    return null;
  }

  public RequestMethod getRequestMethod() {
    return requestMethod;
  }

  public String getPath() {
    return path;
  }

  public List<String> getRequestContentTypeList() {
    return requestContentTypeList;
  }

  public List<String> getResponseContentTypeList() {
    return responseContentTypeList;
  }

  public Class<?> getReturnType() {
    return returnType;
  }

  public List<InvocationParam> getQueryParams() {
    return queryParams;
  }

  public Optional<Object> findPostObject() {
    return Optional.ofNullable(postObject);
  }

  RequestMethod getRequestMethod(final Method method) {
    if (findAnnotation(method, GET.class).isPresent()) {
      return RequestMethod.GET;
    } else if (findAnnotation(method, POST.class).isPresent()) {
      return RequestMethod.POST;
    } else if (findAnnotation(method, PUT.class).isPresent()) {
      return RequestMethod.PUT;
    } else if (findAnnotation(method, DELETE.class).isPresent()) {
      return RequestMethod.DELETE;
    } else if (findAnnotation(method, PATCH.class).isPresent()) {
      return RequestMethod.PATCH;
    } else if (findAnnotation(method, HEAD.class).isPresent()) {
      return RequestMethod.HEAD;
    } else if (findAnnotation(method, OPTIONS.class).isPresent()) {
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
        findAnnotation(method.getDeclaringClass().getAnnotations(), Path.class);
    final Optional<Path> methodPath = findAnnotation(method, Path.class);
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
    return findAnnotation(methodAnnotations, findAnnotation);
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
      final Parameter[] parameters, final Object[] args) {
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
          findAnnotation(param.getAnnotations(), QueryParam.class);
      if (paramAnnotation.isPresent()) {
        final String name = paramAnnotation.get().value();
        final InvocationParam invocationParam = new InvocationParam(name, arg);
        queryParams.add(invocationParam);
      }
    }
    return queryParams;
  }

  private Optional<Object> findPostObject(final Parameter[] parameters, final Object[] args) {
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
      !findAnnotation(param.getAnnotations(), PathParam.class).isPresent() //
          && !findAnnotation(param.getAnnotations(), QueryParam.class).isPresent()) {
        return Optional.ofNullable(arg);
      }
    }
    return Optional.empty();
  }

  private List<InvocationParam> createPathParams(
      final Parameter[] parameters, final Object[] args) {
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
          findAnnotation(param.getAnnotations(), PathParam.class);
      if (paramAnnotation.isPresent()) {
        final String name = paramAnnotation.get().value();
        final InvocationParam invocationParam = new InvocationParam(name, arg);
        pathParams.add(invocationParam);
      }
    }
    return pathParams;
  }

  private String substituteParams(
      final String createPath,
      final List<InvocationParam> queryParams,
      final List<InvocationParam> pathParams) {
    String substituted = createPath;
    for (final InvocationParam queryParam : queryParams) {
      substituted = substituted.replace(queryParam.getName(), queryParam.getValue().toString());
    }
    for (final InvocationParam pathParam : pathParams) {
      substituted =
          substituted.replace("{" + pathParam.getName() + "}", pathParam.getValue().toString());
    }
    return substituted;
  }
}
