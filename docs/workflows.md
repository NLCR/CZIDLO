# CZIDLO core workflows

## Overview

This document describes the workflows that are the most important for understanding CZIDLO as a system.

## Reservation of URN:NBN identifiers

CZIDLO supports reservation of a new batch of URN:NBN identifiers for a registrar.

Typical endpoint family:

```text
POST /api/v6/registrars/{REGISTRAR_CODE}/urnNbnReservations
GET  /api/v6/registrars/{REGISTRAR_CODE}/urnNbnReservations
```

## Registration of a digital document

A core workflow is registration of a digital document.

Typical endpoint:

```text
POST /api/v6/registrars/{REGISTRAR_CODE}/digitalDocuments
```

The client sends XML metadata describing the digital document.

The registration mode is not sent explicitly. Instead, it is determined by backend logic from the presence and state of `urn:nbn` inside the submitted XML data.

### Supported registration modes

#### `BY_RESOLVER`

If the XML data does not contain any `urn:nbn`, the registration is processed in `BY_RESOLVER` mode.

In this mode, the system assigns the URN:NBN during registration.

#### `BY_RESERVATION`

If the XML data contains a reserved `urn:nbn`, the registration is processed in `BY_RESERVATION` mode.

In this mode, a previously reserved identifier is used.

#### `BY_REGISTRAR`

If the XML data contains a free `urn:nbn` with the registrar's code, the registration is processed in `BY_REGISTRAR` mode.

In this mode, the registrar supplies the identifier directly.

### Registration mode permissions

Each registrar has its own configuration determining which registration modes are allowed.

Each mode is enabled or disabled independently.

This means that successful registration depends on both:

- the data sent in the XML payload
- the registration mode settings configured for the registrar

## Resolution of URN:NBN

A central public workflow is resolving URN:NBN identifiers.

Typical endpoint:

```text
GET /api/v6/resolver/{URN:NBN}
```

Examples:

```text
https://resolver.nkp.cz/urn:nbn:cz:aba001-0001qc
https://resolver.nkp.cz/resolver/urn:nbn:cz:aba001-0001qc
https://resolver.nkp.cz/api/v6/resolver/urn:nbn:cz:aba001-0001qc
```

### Default behavior

If no explicit output format is requested, the resolver typically performs an HTTP redirect.

Depending on the state of the resolved record, it may redirect:

- to the URL of an active digital instance
- or to a search page in the web client if there is no more suitable direct target

### Structured output

If `format=xml` or `format=json` is specified, the resolver returns structured metadata instead of redirecting.

The `digitalInstances` query parameter controls whether digital instances are included in the returned metadata.

### Edge cases

Depending on the state of the URN:NBN, the resolver may return:

- metadata for an active digital document
- a record describing the URN:NBN state
- an error if the identifier is invalid
- an error if the identifier is unknown
- an error if the identifier has been deactivated

## Updating partially empty metadata

The resolver endpoint also supports a `PUT` operation for updating the digital document and intellectual entity record.

Typical endpoint:

```text
PUT /api/v6/resolver/{URN:NBN}
```

This operation fills only empty values and does not replace already populated values blindly.

## Digital instances

Digital instances can be attached to already registered digital documents.

Typical endpoints:

```text
POST /api/v6/resolver/{URN:NBN}/digitalInstances
GET  /api/v6/resolver/{URN:NBN}/digitalInstances
```

There is also an alternative path using registrar-scope identifiers:

```text
POST /api/v6/registrars/{REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/{ID_TYPE}/{ID_VALUE}/digitalInstances
GET  /api/v6/registrars/{REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/{ID_TYPE}/{ID_VALUE}/digitalInstances
```

This makes it possible to attach digital instances using either:

- the assigned URN:NBN
- or a registrar-scope identifier within the registrar context

## Registrar-scope identifiers

CZIDLO also supports operations using registrar-scope identifiers.

These identifiers allow systems to work with local identifiers in the context of a registrar and later map them to the assigned URN:NBN and related metadata.

## Search and statistics

Search and statistics belong primarily to the web layer.

They rely on indexed data in Elasticsearch and are exposed through the administrative frontend and `web-api`.

Without Elasticsearch:

- core API operations still work
- assignment and resolution still work
- user management can still work through `web-api`
- but search and statistics will not function correctly

## API references

Important API v6 endpoint families include:

- `/v6/registrars`
- `/v6/registrars/{REGISTRAR_CODE}`
- `/v6/registrars/{REGISTRAR_CODE}/urnNbnReservations`
- `/v6/registrars/{REGISTRAR_CODE}/digitalDocuments`
- `/v6/registrars/{REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/{ID_TYPE}/{ID_VALUE}`
- `/v6/resolver/{URN:NBN}`
- `/v6/resolver/{URN:NBN}/digitalInstances`
- `/v6/urnnbn/{URN:NBN}`

For `web-api`, the primary technical reference is the OpenAPI documentation:

- `https://resolver.nkp.cz/web-api/api-docs/`
