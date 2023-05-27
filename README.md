

# FirebaseManager [![](https://jitpack.io/v/vadim1690/FirebaseManager.svg)](https://jitpack.io/#vadim1690/FirebaseManager)



FirebaseManager is a library that simplifies the management of Firebase real-time database operations in Android applications.  
It provides a convenient way to read, write, and remove data from the Firebase database.                                       
With FirebaseManager, you can easily retrieve lists of items, filter and sort them, perform single-item reads, and interact with LiveData objects for real-time updates.
It abstracts away the complexity of Firebase database operations, allowing you to focus on building robust and efficient Firebase-powered features in your Android app.

# Getting Started

## Installation

1) Add it in your root `build.gradle` at the end of repositories:

	    allprojects {
        repositories {
          ...
          maven { url 'https://jitpack.io' }
          }
        }
      
------------------------------------------------------------------------------------------------------------

2) Add the following dependencies in you app level gradle file if not exists:

	    dependencies {
			implementation 'com.github.vadim1690:FirebaseManager:1.0.0.1'
		}

------------------------------------------------------------------------------------------------------------

## Prerequisites

To use FirebaseManager, you need to have the following:

   * Android Studio installed
   * A minimum of Android API 21

Every model that will be saved to Firebase Realtime Database must have a unique id as a String property and a getter for this field.

	public class MyModel {

	    private String id;

	    public MyModel(String id) {
		this.id = id;
	    }

	    public String getId() {
		return id;
	    }
	}

------------------------------------------------------------------------------------------------------------


## Usage

Initialize the FirebaseManager by creating an instance of it with the desired table name, item class, and key mapper function:

          public class MainActivity extends AppCompatActivity {
              
              @Override
              protected void onCreate(Bundle savedInstanceState) {
                  super.onCreate(savedInstanceState);
                  setContentView(R.layout.activity_main);

		      FirebaseManager<MyModel> firebaseManager = new FirebaseManager<>("my_table", MyModel.class, MyModel::getId);
              }
          }

   The "my_table" parameter represents the name of the Firebase database table you want to work with.
   Replace `MyModel` with your own model class, and `getId` with the appropriate key mapper function for your model.
   Now you're ready to perform various operations on the Firebase database using the FirebaseManager!
   Feel free to modify and enhance this initialization section as per your requirements and additional instructions you may want to provide to users.


## Main Methods

## `getListLiveData()`
    
   Retrieve a LiveData object that provides a list of `MyModel` items from the Firebase database table named "my_table".
     
   * Example
   
   
			FirebaseManager<MyModel> firebaseManager = new FirebaseManager<>("my_table", MyModel.class, MyModel::getId);
			LiveData<List<MyModel>> itemsLiveData = firebaseManager.getListLiveData();
          


## `getListLiveData(Comparator<T> comparator)`
   
   Retrieve a LiveData object that provides a sorted list of `MyModel` items from the Firebase database table named "my_table", based on the name field.
    
  * Parameters
   
    `comparator` - comparator to compare the model objects.
      
  * Example
    
		FirebaseManager<MyModel> firebaseManager = new FirebaseManager<>("my_table", MyModel.class, MyModel::getId);
		LiveData<List<MyModel>> sortedItemsLiveData = firebaseManager.getListLiveData(Comparator.comparing(MyModel::getName));
		
		
		
## `getListLiveData(Predicate<T> predicate)`
   
   Retrieve a LiveData object that provides a filtered list of `MyModel` items from the Firebase database table named "my_table".
   Only items with a quantity greater than 0 will be included.
    
  * Parameters
   
    `predicate` - predicate to filter the model objects.
      
  * Example
    
		FirebaseManager<MyModel> firebaseManager = new FirebaseManager<>("my_table", MyModel.class, MyModel::getId);
		Predicate<MyModel> filterPredicate = item -> item.getQuantity() > 0;
		LiveData<List<MyModel>> filteredItemsLiveData = firebaseManager.getListLiveData(filterPredicate);	
		
		
## `getListLiveData(Predicate<T> predicate, Comparator<T> comparator)`
   
   Retrieve a LiveData object that provides a filtered and sorted list of `MyModel` items from the Firebase database table named "my_table".
   Only items with the category "Electronics" will be   included, and the list will be sorted based on the item's name.
    
  * Parameters
   
    `predicate` - predicate to filter the model objects.
    
    `comparator` - comparator to compare the model objects.
      
  * Example
    
		FirebaseManager<MyModel> firebaseManager = new FirebaseManager<>("my_table", MyModel.class, MyModel::getId);
		Predicate<MyModel> filterPredicate = item -> item.getCategory().equals("Electronics");
		Comparator<MyModel> nameComparator = Comparator.comparing(MyModel::getName);
		LiveData<List<MyModel>> filteredAndSortedItemsLiveData = firebaseManager.getListLiveData(filterPredicate, nameComparator);		

    

    
