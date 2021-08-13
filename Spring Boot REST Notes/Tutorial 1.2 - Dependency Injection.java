https://www.baeldung.com/spring-autowire



/*------------------------------------ Dependency Injection Summary ------------------------------------------------------- 
    - Used to inject objects into classes so that we can use their method (aka they are dependencies for the class
    - Dependency Injection is LOOSE COUPLING (meaning that we can change code once and since its being imported/injected, it will change everywhere)
    - Fixes TIGHT COUPLING: Why cant we just use `new ...` to instantiate the object in the class and use it? (TIGHT COUPLING -> BAD)
        1) TIGHT COUPLING issues -> Will be hard to change the object later 
            ex: say we want to change the sorting algo, we will need to change everywhere manually. With LOOSE COUPLING, we can chnage it once, and since its imported/injected, it will be chnaged everywhere
        2) everythime we create a object with `new` in the class, we will allocate space on the heap for that object (BAD)
        3) dont know when they will be garbage collected
        4) Will be difficult to trace issues since we instantiate it every time
        5) Will have issues tracing issue (if we make a db instance, what if db goes down? will be hard to figure out where the issue is)
    

*/

// TIGHT COUPLING EXAMPLES  
    // EX 1 (BAD) 
        public class SomeBusinessService{
            SortingAlgorithm sortingAlgorithm = new BubbleSortAlgorithm();    
        }
        public class BubbleSortAlgorithm implements SortingAlgorithm{}

    // EX 2 (BAD) 
        public class SomeBusinessService{
            SortingAlgorithm sortingAlgorithm;    
            public SomeBusinessService(SortingAlgorithm sortingAlgorithm){      // ERROR! sortingAlgorithm isnt instantiazed so will have errors
                this.sortingAlgorithm = sortingAlgorithm;                       // code will only work if i do `this.sortingAlgorithm = new BubbleSortAlgorithm()` here (also TIGHT COUPLING)
            }   
        }
        public class BubbleSortAlgorithm implements SortingAlgorithm{}



/* LOOSE COUPLING EXAMPLES (Dependency Injection)
    * @Service or @Component -> Tells Spring to manage this class so it can find it later
    * @Inject or @Autowired  -> Looks at @Service or @Component for the class, makes a new instances and injects it here                                    */

    // EX 1: Using @Component and @Autowired - @Component lets Spring find the correct class when u inject it
        @Component
        public class SomeBusinessService{
            @Autowired                                                          // *** 2) Tells spring that SortingAlgorithm is a dependency. Sopring will look at the @Components for it and inject it here.
            SortingAlgorithm sortingAlgorithm;      
        }   

        @Component                                                              // *** 1) Tells Spring to manage this class
        public class BubbleSortAlgorithm implements SortingAlgorithm{}

    // EX 2: With a Constructor
        @Component
        public class SomeBusinessService{
            SortingAlgorithm sortingAlgorithm;                                  
            public SomeBusinessService(SortingAlgorithm sortingAlgorithm){      // Lets anyone choose which sorting algo they wana use (use @Autoinject, @Service??)
                this.sortingAlgorithm = sortingAlgorithm;
            }                
        }
        @Component
        public class BubbleSortAlgorithm implements SortingAlgorithm{}




    //* Perferred Way 1: Inject a Service into Constructor:
        public class SomeController{
            private final SomeService someService;
            @Autowired                                                      // 1) Use this decorator to say 
            public SomeController(SomeService someService){                 //    `SomeService` will be injected into `SomeController()` (constructor)    
                this.someService = someService;
            }
        }    

        @Service                                                            // 2) Set it as a Service class
        public class SomeService{
            public String hello(){return "Hello!"}
        }


        // Ex of this:
            public class SomeBusinessService{
                private final SortingAlgorithm sortingAlgorithm;  
                @Autowired                                                                              
                public SomeBusinessService(SortingAlgorithm sortingAlgorithm){    
                    this.sortingAlgorithm = sortingAlgorithm;
                }                
            }
            @Component
            public class BubbleSortAlgorithm implements SortingAlgorithm{}

    //* Injecting a Primary Component (example without constructor)
        class PC{
            @Autowired
            private final ardDrive hd1;                                     // 2) Spring will look for a HardDrive to inject. I found two: WesternDigitalHD and SeagateHD -> SeagateHD is the primary one
        }

        interface HardDrive{}                                               // 1a) Hardrives will implement this interface

        @Primary                                                            // 1c) @Primary - telling spring whinch one to import. Since the two classes impleement Hardrive, When i inject Hardrive ->  Spring will see these two and will be confused. Primary tells Spring which to inject (SeagateHD in this case)               
        @Component                                                          // 1b) Made a SegateHD Hardrive (Spring will manage this with HardDrive)
        class SegateHD implements HardDrive{...}

        @Component                                                          // 1b) Made a WesternDigitalHD Hardrive (Spring will manage this with HardDrive)
        class WesternDigitalHD implements HardDrive{...}

      




//** Dependency Injection Main Example
    // Interfaces
        public interface SortingAlgorithm {
            public void sort(int[] numbers);                       // Will implement a sorting algorithm that will implement this interface
        }
        public interface SearchingAlgorithm {
            public int search(int[] arr, int n);
        }

    // Implemented classes 
        @Component
        public class BubbleSort implements SortingAlgorithm{
            @Override
        public void sort(int[] arr) { /* will sort the array in place*/ }
        }

        @Component
        public class BinarySearch implements SearchingAlgorithm{
            @Override
            public int search(int[] sortedArr, int n) { }
    // main
        public static void main(String[] args) {
            ApplicationContext applicationContext = SpringApplication.run(UdemyTutorialApplication.class, args);
            SortingAlgorithm sortingAlgorithm = applicationContext.getBean(SortingAlgorithm.class);						//!** Can import `SortingAlgorithm.class` instead (but i need to label one as @Primary since i have two classes implementing it). @Component is added to BubbleSort and QuickSort (in this case, one need to have @Primary) -> Spring can figure out what i want
            SearchingAlgorithm searchingAlgorithm = applicationContext.getBean(BinarySearch.class);						//!**  Can also leave it as `SearchingAlgorithm.class` instead of `BinarySearch.class` since we only have one class implementing SearchingAlgorithm !
        }