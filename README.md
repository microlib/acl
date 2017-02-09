# ACL info

## Environment

Ensure JDK 1.8.0 or later is installed

## Generate RSA signing files via shell:

A key pair for each service should be generated and stored in a secure unique directory (for each key pair).

```
$ openssl genrsa -out private_key.pem 2048
$ openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem -out private_key.der -nocrypt
$ openssl rsa -in private_key.pem -pubout -outform DER -out public_key.der

```

## Compile

```
./make.sh clean
./make.sh compile

```

## Launch the server

```
./make.sh run

```

## Testing using curl

The folder test-keystore is used for testing and should not be used for any production environments

```
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X POST -d '{"controller":"com.microlib.controller.JwtService" , "action":"getToken"}' http://localhost:9000
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X POST -d '{"controller":"com.microlib.controller.JwtService" , "action":"createAndSignToken", "key-id":"test-keystore"}' http://localhost:9000
curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X POST -d '{"controller":"com.microlib.controller.JwtService" , "action":"verifyToken", "key-id":"test-keystore" , "token": "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJhdXRoMCJ9.gF-JDds389H5l4tk2o7qpuSIzSAEgjfVwTb7c3Tf1InuD7EWk5gjY4kKPP__MGc39HfOobjqUMsUFAJBAJYOJxKmfLMBCLr5TXMMeLcc3-qZw3NZ0DDhq76yLiVA_P3pBm1k-kKtZQvwRY8VrLN9JfBm0BDy3f2wvNRmDXQLHAU33fi4zACpGcTJ9TfNBoY84sOGUBhd73yxPLr4lBhYrFjcqGboZDNzg2LdisTVP1I_9KlHA4d8-H5LHYOcwiFD-hFZteKl52jslKfNucHgrhn0D1iLf4YiE92yNVobLAkVN_qPG8ZX8sNlA5AahIqKenk6hK_C0f1LTGzc6ZxXMA"}' http://localhost:9000

```

## Improvements

Read all key pairs into memory at server startup