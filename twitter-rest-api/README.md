# EmoTwit REST API

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_** Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## API Docs

```javascript
openapi: 3.0.3
info:
  title: EmoTwit APIs
  description: APIs to search tweets and classify their sentiments in real-time
  license:
    name: MIT
    url: http://localhost:8080
  version: 1.0.0
tags:
- name: Tweet
  description: Tweets
- name: EmoTwit Resources
  description: EmoTwit REST APIs
paths:
  /api/clear:
    delete:
      tags:
      - EmoTwit Resources
      summary: Clear list of tweets
      description: Delete all existing tweets inside the list
      operationId: clearTweets
      responses:
        "204":
          description: Tweet list cleared
          content:
            application/json: {}
        "400":
          description: Request not valid
          content:
            application/json: {}
  /api/search:
    post:
      tags:
      - EmoTwit Resources
      summary: Search tweets
      description: Search tweets by entering a keyword and add them to the list
      operationId: searchTweets
      parameters:
      - name: keyword
        in: query
        description: Search query
        required: true
        schema:
          type: string
      responses:
        "201":
          description: Tweets added
          content:
            application/json: {}
  /api/view:
    get:
      tags:
      - EmoTwit Resources
      summary: Get tweet list
      description: Get all tweet information inside the list
      operationId: getTweets
      responses:
        "200":
          description: Operation complete
          content:
            application/json: {}
components:
  schemas:
    Tweet:
      description: Tweet profile or representation
      type: object
      properties:
        id:
          format: int64
          type: integer
        score:
          format: double
          type: number
        sentiment:
          type: string
        username:
          type: string
        idstring:
          type: string
        displayname:
          type: string
        content:
          type: string
        twitcon:
          type: string
```

## Related Guides

- REST Client ([guide](https://quarkus.io/guides/rest-client)): Call REST services
- RESTEasy JAX-RS ([guide](https://quarkus.io/guides/rest-json)): REST endpoint framework implementing JAX-RS and more

## Provided Code

### REST Client

Invoke different services through REST with JSON

[Related guide section...](https://quarkus.io/guides/rest-client)

### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)
