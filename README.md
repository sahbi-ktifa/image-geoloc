# Image Geoloc API [![Build Status](https://travis-ci.org/sahbi-ktifa/image-geoloc.svg?branch=master)](https://travis-ci.org/sahbi-ktifa/image-geoloc)

This project aims to offer an API in order to determine image GPS location within specific bounds.
This project uses Drew Noakes [metadata-extractor](https://github.com/drewnoakes/metadata-extractor) library.

## How to use API
This API is available at https://image-geoloc.herokuapp.com/

### Picture within specific bounds
Do a `POST` request at `/api/geo/within` with following parameters:
- `file` -> Form-data parameter
- `latitudeA` -> Request parameter as double for point A
- `longitudeA` -> Request parameter as double for point A
- `latitudeB` -> Request parameter as double for point B
- `longitudeB` -> Request parameter as double for point B

A and B will be used to determine area boundaries.

Example call:
https://image-geoloc.herokuapp.com/api/geo/within?latitudeA=48.87&longitudeA=1.76&latitudeB=48.85&longitudeB=1.81