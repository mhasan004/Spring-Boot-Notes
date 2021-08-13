// Tutorial: https://www.udemy.com/course/spring-tutorial-for-beginners/
/*___________________________ 1) Setting up Spring Framework ________________________________________________________________*/
    // Start up a project fast -> https://start.spring.io/
    // dont use the snap shot versions


        
//?____________________ Final Code for Session 3 with Dependency Injection [ @Primary, @Component,  @Autowired, ApplicationContext (.getbean()) ]________________________________________________________________________*/
    // Interfaces
    public interface SortingAlgorithm {
        public void sort(int[] arr);                                               
    }
    public interface SearchingAlgorithm {
        public int search(int[] arr, int n);
    }
// Sorting Algorithms
    @Primary                                                                                        
    @Component 
    public class BubbleSort implements SortingAlgorithm{
        @Override
        public void sort(int[] arr) {/* Will sort the array in place */}
    }

    @Component 
    public class QuickSort implements SortingAlgorithm{
        @Override
        public void sort(int[] arr) {/* Will sort the array in place */}
    }
// Searching Algorithm (dependent on SortingAlgorithm)
    @Component 
    public class BinarySearch implements SearchingAlgorithm{
        @Autowired
        private SortingAlgorithm sortingAlgorithm;

        @Override
        public int search(int[] arr, int n) {
            int[] sorted = sortingAlgorithm.sort(arr);
            /* Search the array fro n -> return index */
        }
    }
// Main
    @SpringBootApplication     
    public class Tutorial {
        public static void main(String[] args) {
            ApplicationContext applicationContext = SpringApplication.run(Tutorial.class, args);	            // Getting Application COntext to get the bean
            SearchingAlgorithm searchingAlgorithm = applicationContext.getBean(SearchingAlgorithm.class);		// Geting the bean -> go rid of the old way! :)		

            int result = searchingAlgorithm.search(new int[] {12, 3, 4}, 12);
        }
    }

















/*___________________________ 3) LVL 1 - Loose Coupling (no Dependency Injection yet) _____________________________________________________________________________*/
    // NOTICE: No @Autowired, @Components, using new operator (since there is no dependency injection yet)
    // Interfaces
        public interface SortingAlgorithm {
            public void sort(int[] arr);                                               
        }
        public interface SearchingAlgorithm {
            public int search(int[] arr, int n);
        }

    // Sorting Algorithms
        public class BubbleSort implements SortingAlgorithm{
            @Override
            public void sort(int[] arr) {/* Will sort the array in place */}
        }

        public class QuickSort implements SortingAlgorithm{
            @Override
            public void sort(int[] arr) {/* Will sort the array in place */}
        }

    // Searching Algorithm (dependent on SortingAlgorithm)
        public class BinarySearch implements SearchingAlgorithm{
            private SortingAlgorithm sortingAlgorithm;
            public BinarySearch(SortingAlgorithm sortingAlgorithm){                         // Let user choose which sort algo to use for binary search
                this.sortingAlgorithm = sortingAlgorithm
            }

            @Override
            public int search(int[] arr, int n) {
                int[] sorted = sortingAlgorithm.sort(arr)
                /* Search the array fro n -> return index */
            }
        }
    // Main
        @SpringBootApplication     
        public class Tutorial {
            public static void main(String[] args) {
                BinarySearch binarySearch = new BinarySearch(new QuickSort());              //! BAD (using new keyword) - Will fix by @Autowired -> will inject the dependency directly later to avoid doing this
                int result = binarySearch.search(new int[] {12, 3, 4}, 12);
            }
        }



/*___________________________ 4) LVL 2 - Beans + Dependency Injection (Using Spring to Manage Beans) (@Component, @Autowired)_______________________________________________________________
    //!------------------------------ Need to tell Spring 3 things: ------------------------------------------------------------------------------------------
        1) What are the different beans that Spring has to manage?                                                      */
            @Component                                              /* Adding this on top of a class ->tells Spring to manage the class -> bean     
        2) What are the dependencies for a bean? ex: need to tell Spring that the dependency for `BinarySearch` is `SortingAlgorithm` )                                          */
            @Autowired                                              /* Dependency Injection -> says that this class (BinarySearch) depends on `SortingAlgorithm`
        3) Where should Spring search fot the Bean?
            Whatever package `@SpringBootApplication` is in -> Spring boot will scan that package so no need to do this step!
    
        * Getting a Bean (Spring is managing all the beans (@Components, etc), can now get them with ApplicationContext):                                                                                 */
            ApplicationContext applicationContext = SpringApplication.run(MainAppClass.class, args);        // applicationContext - lets us access the ApplicationContext to be able to import beans
            SortingAlgorithm sortingAlgorithm = applicationContext.getBean(SortingAlgorithm.class);		    // getting a bean
    //!----------------------------------------------------------------------------------------------------------------------------------------------------------*/

    // Sorting Algorithms - makign them @Component beans so that when we inject using @Autowired, Spring can find them! 
        @Primary                                                                                        // Explained in step 6) cant have two component sof same type without one being primary -> for @AutoWired         
        @Component                                                                                      //! Telling Spring to manage this (When i do a @Autowired on a SortingAlgorithm variable -> Spring will search and will try to firgure out which oen i want)
        public class BubbleSort implements SortingAlgorithm{}
        @Component
        public class QuickSort implements SortingAlgorithm{}

    // Searching Algorithm - Inject SortingAlgorithm dependency now
        public class BinarySearch implements SearchingAlgorithm{
            @AutoWired                                                                                  //! Injecting SortingAlgorithm dependency (BubbleSort inbjected since its a component of type SortingAlgorithm and is the primary)
            private SortingAlgorithm sortingAlgorithm;
            public BinarySearch(SortingAlgorithm sortingAlgorithm){                        
                this.sortingAlgorithm = sortingAlgorithm;
            }
            //...
        }
    // Main - Getting Beans and avoiding new keyword
        @SpringBootApplication     
        public class Tutorial {
            public static void main(String[] args) {
                //// BinarySearch binarySearch =  new BinarySearch(new QuickSort());                      // :) ridding new keyword
                ApplicationContext applicationContext = SpringApplication.run(Tutorial.class, args);	            // Getting Application COntext to get the bean
                SearchingAlgorithm searchingAlgorithm = applicationContext.getBean(SearchingAlgorithm.class);		// Geting the bean -> go rid of the old way! :)		

                int result = searchingAlgorithm.search(new int[] {12, 3, 4}, 12);
            }
        }



/*___________________________ 5) What's Happening in the Background __________________________________________________________
    //!------------------------------ Whats Happening in the Background (Hows Spring managing beans) ------------------------------------------------------------------------------------------
        Log all the things Spring do: application.properties  -> logging.level.org.springframework = debug    

        1) Spring goes through the package (where @SpringBootApplication is in) and finds all @Components, @Services, etc classes
        2) Once Spring finds all the stuff that it needs to manmage 
            -> will start creating the bean instances for each class and will try to identify the dependencies (constructor injection vs setter injection)
    //!----------------------------------------------------------------------------------------------------------------------------------------------------------*/


/*___________________________ 6) Dynamic Autowiring (@Primary) _________________________________________________________________________*/
    @Primary                                                                //! Spring will find two @Components with of `SortingAlgorithm` type -> wont know which to auto inject
    @Component                                                                                      
    public class BubbleSort implements SortingAlgorithm{}
    
    @Component
    public class QuickSort implements SortingAlgorithm{}



/*___________________________ 7) Three Types of Autowiring (Constructor vs Setter Injection vs nothing) ________________________________________________________________________*/
    //!------------------------------ Three Types of Dependency Injection ------------------------------------------------------------------------------------------
        // 1) Dont need to user Setter or Constructor (Also called setter injection)
            // Ex: Say `SortingAlgorithm` interface is implemented by two classe (both @Components)
            // AutoWiring by @Primary       - Two @Components implements SortingAlgorithm -> but one is @Primary
                @AutoWired                                                                    
                private SortingAlgorithm sortingAlgorithm;                          //* (Best way) Dynamic Injection - defaults to BubbleSort if its set to @Primary
            // AutoWiring by Name           - Two @Components implements SortingAlgorithm -> but none has @Primary 
                @AutoWired                                                          //* (2nd Best way) Picking to use `BubbleSort` using @Qualifier 
                @Qualifier("bubble")                                                                   
                private SortingAlgorithm sortingAlgorithm;                         
            // AutoWiring by Variable Name  - Two @Components implements SortingAlgorithm -> but none has @Primary 
                @AutoWired                                                                    
                private SortingAlgorithm bubbleSort;                                /* Will take in BubbleSort from the name! Spring is smart enough to figure it out!
 
        2) Constructor Injection (Uses a constructor) - lets u pick algo if u want
            @AutoWired                                                                                             
            private SortingAlgorithm sortingAlgorithm;           
            public BinarySearch(SortingAlgorithm sortingAlgorithm){                        
                this.sortingAlgorithm = sortingAlgorithm;
            }
        3) Setter Injection - lets u set algo if u want
            @AutoWired                                                                    
            private SortingAlgorithm sortingAlgorithm;
            public void setSortingAlgorithm(SortingAlgorithm sortingAlgorithm){                        
                this.sortingAlgorithm = sortingAlgorithm;
            }
    //!----------------------------------------------------------------------------------------------------------------------------------------------------------*/

    






/*___________________________ 1) Setting up Spring Framework ________________________________________________________________*/
/*___________________________ 2) Tight Coupling _____________________________________________________________________________*/
/*___________________________ 3) Loose Coupling _____________________________________________________________________________*/
/*___________________________ 4) Using Spring to Manage Beans _______________________________________________________________*/
/*___________________________ 5) Whats happening in the background __________________________________________________________*/
/*___________________________ 6) Dynamic Autowiring _________________________________________________________________________*/
/*___________________________ 7) Types of Autowiring ________________________________________________________________________*/
/*___________________________ 8) Spring Modules _____________________________________________________________________________*/
/*___________________________ 9) Spring Projects ____________________________________________________________________________*/
/*__________________________ 10) Whys Spring Popular ________________________________________________________________________*/

