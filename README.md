# Howto

sudo docker run --name howtodb -e POSTGRES_DB=howtodb -e POSTGRES_USER=howto -e POSTGRES_PASSWORD=password -p 5432:5432
-d postgres:14.5