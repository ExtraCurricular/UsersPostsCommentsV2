Welcome to users, posts, comments api service! </br>

docker-compose up -d </br>
port: 80 </br>

Users endpoint: </br>
api/users (POST) - in body - email(string), username(string) </br>
api/users (GET) - no params </br>
api/users/id (GET) - in url - id </br>
api/users/id (DELETE) - in url - id </br>
api/users/id (PUT) - in url - id - in body - email(string), username(string) </br>
api/users/id (PATCH) - in url - id - in body - email(string), username(string) </br>

Posts endpoint: </br>
api/posts (POST) - in body - userId(int), title(string), body(string) </br>
api/posts (GET) - no params </br>
api/posts/id (GET) - in url - id </br>
api/posts/id (DELETE) - in url - id </br>
api/posts/id (PUT) - in url - id - in body - userId(int), title(string), body(string) </br>
api/posts/id (PATCH) - in url - id - in body - userId(int), title(string), body(string) </br>

Comments endpoint: </br>
api/comments (POST) - in body - userId(int), postId(int), body(string) </br>
api/comments (GET) - no params </br>
api/comments/id (GET) - in url - id </br>
api/comments/id (DELETE) - in url - id </br>
api/comments/id (PUT) - in url - id - in body - userId(int), postId(int), body(string) </br>
api/comments/id (PATCH) - in url - id - in body - userId(int), postId(int), body(string) </br>
