conn = new Mongo("localhost:27017");

db = conn.getDB("gutenbergAdmin");

db.users.drop();

db.users.insert(
{
    "_id" : ObjectId("5a28f2b0acc04f7f2e9740ae"),
    "_class" : "com.dub.spring.domain.MyUser",
    "username" : "Admin",
    "hashedPassword" : BinData(0, "JDJhJDEwJGU2MEdhLzgyNFpsWGE4SUZQcHFUVWVmUVRUOGxCR2dNckpCWk81c3hjOTNFb0toenVhM2xT"),
    "accountNonExpired" : true,
    "accountNonLocked" : true,
    "credentialsNonExpired" : true,
    "enabled" : true,
    "authorities" : [
		{
			"authority" : "ROLE_USER"
		}
	],
    "addresses" : [
        {
            "street" : "Main Street 2233",
            "city" : "Dallas",
            "zip" : "75215",
            "state" : "TX",
            "country" : "USA"
        }
    ],
    "paymentMethods" : [
        {
            "cardNumber" : "1234567813572468",
            "name" : "Carol Baker"
        }
    ],
    "mainPayMeth" : 0,
    "mainShippingAddress" : 0
});


