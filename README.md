# gutenberg-replica
A MongoDB based  e-commerce site demo with Spring Boot support and replication.

I present here a MongoDB based e-commerce web site demo. The focus is on MongoDB Spring Boot support.

The database gutenbergRS used in this demo can be prepopulated using the attached Javascript file gutenbergRS.js.

Five collections are used:

categories: book categories

books: books

orders: orders to be placed or already placed on this site

user: users registered to this site

reviews: reviews of books written by registered users

All orders can be persisted with a state that can be: CART, PRE_AUTHORIZE, PRE_SHIPPING, SHIPPED

A user can see other books most frequently bought with the books she plans to buy. A user can vote for the reviews written for these books

In addition to the main application a standalone application GutenbergReplicaMonitor displays the current status of the replica set. It runs on a different port 8090 with context path gutenberg-admin. Stomp over websockets is used for status display.   

A full tutorial for this project can be found on this page:

www.dominique-ubersfeld.com/GUTENBERG/GutenbergRS.html
