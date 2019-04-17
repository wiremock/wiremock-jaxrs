# Wiremock JAX-RS
[![Build Status](https://travis-ci.org/tomasbjerre/wiremock-jaxrs.svg?branch=master)](https://travis-ci.org/tomasbjerre/wiremock-jaxrs)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.bjurr.violations/wiremock-jaxrs/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.bjurr.violations/wiremock-jaxrs)
[![Bintray](https://api.bintray.com/packages/tomasbjerre/tomasbjerre/se.bjurr.violations%3Awiremock-jaxrs/images/download.svg) ](https://bintray.com/tomasbjerre/tomasbjerre/se.bjurr.violations%3Awiremock-jaxrs/_latestVersion)

[Wiremock](http://wiremock.org/) with JAX-RS support. Enables creation of stubs from JAX-RS annotated resources. It:

 * Automates configuration of [stubs](http://wiremock.org/docs/stubbing/) for those using JAX-RS.
 * Contains validation checks against JAX-RS which enables you to produce **type safe stubs**.

Given:

 * JAX-RS annotated resource
 * Called method
 * Response (unless void)

It will create a [Wiremock stub](http://wiremock.org/docs/stubbing/) by gathering information from the JAX-RS annotations on the given resource.

## Usage

It extends, and works just like, [Wiremock](http://wiremock.org/docs/stubbing/) by adding a new factory method:
```java
WiremockJaxrs.invocation(Class<T> resource, ResourceInvocation<T> invocation)
```

That is used like:
```java
import static com.github.tomakehurst.wiremock.client.WireMockJaxrs.invocation;
...
invocation(TestResouce.class, (r) -> r.whateverMethod(anyParameterValue))
```

### Example

When invoked like this:

```java
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMockJaxrs.invocation;
...
final List<ItemDTO> responseObject = Arrays.asList(new ItemDTO("pong"));
final StubMapping sm =
    stubFor( //
        invocation(ItemResouce.class, (r) -> r.getItems()) //
            .willReturn(aResponse().withStatus(SC_ACCEPTED), responseObject));
```

It creates a stub (as described [here](http://wiremock.org/docs/stubbing/)):

```json
{
  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
  "request" : {
    "urlPattern" : ".*/list$",
    "method" : "GET",
    "headers" : {
      "Accept" : {
        "equalTo" : "application/json"
      }
    }
  },
  "response" : {
    "status" : 202,
    "body" : "[{\"str\":\"pong\",\"id\":0}]",
    "headers" : {
      "Content-Type" : "application/json"
    }
  },
  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
}
```

When `TestResource` looks like:

```java
@Path("/")
public interface ItemResouce {

  @Path("/list")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<ItemDTO> getItems();

  @Path("/create")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ItemDTO post(ItemDTO item);
}
```

If the method consumes content, that content is also matched. When invoked like this:
```java
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMockJaxrs.invocation;
...
final ItemDTO responseObject = new ItemDTO("the item");
responseObject.setId(123);
final ItemDTO postedItem = new ItemDTO("the item");

final StubMapping sm =
    stubFor( //
        invocation(ItemResouce.class, (r) -> r.post(postedItem)) //
            .willReturn(aResponse().withStatus(SC_ACCEPTED), responseObject));
```

It creates a stub (as described [here](http://wiremock.org/docs/stubbing/)):

```json
{
  "id" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece",
  "request" : {
    "urlPattern" : ".*/create$",
    "method" : "POST",
    "headers" : {
      "Content-Type" : {
        "equalTo" : "application/json"
      },
      "Accept" : {
        "equalTo" : "application/json"
      }
    },
    "bodyPatterns" : [ {
      "equalToJson" : "{\"str\":\"the item\",\"id\":0}",
      "ignoreArrayOrder" : true,
      "ignoreExtraElements" : true
    } ]
  },
  "response" : {
    "status" : 202,
    "body" : "{\"str\":\"the item\",\"id\":123}",
    "headers" : {
      "Content-Type" : "application/json"
    }
  },
  "uuid" : "d68fb4e2-48ed-40d2-bc73-0a18f54f3ece"
}
```

Check the test cases in this repository for more examples!
