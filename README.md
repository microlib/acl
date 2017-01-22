# acl

##Access Control List

###Proposed Architecture

The main actors in this proposal are :-
- User
- Client (web or mobile app)
- OAuth 2.0 server (AS)
- Resource Service (the actual resource used/stored RS)

A basic flow with OAuth and JWT (Json Web Token)
- The client (web or mobile app) requests access to the resource service by calling the OAuth server with jwt capabilities
- The OAuth server redirects to allow the User to authenticate
- The Authorization Server then validates the user credentials and provides an Access Token and a JWT to the client.
- The client uses the JWT and will typical store the user data in its own session
- The client then sends the access token to the Resource Server
- The Resource Server responds sending the relevant data to the client.
- The JWT can then be used for access to other services.

The JWT establishes a trust between the AS and the client. 

Info on JWT can be found here https://en.wikipedia.org/wiki/JSON_Web_Token
