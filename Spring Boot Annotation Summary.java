import org.graalvm.compiler.lir.CompositeValue.Component;

/*File Structure of a Spring Boot Project:
    src
        main
            java                            - * Where our code goes
            resources            
                application.properties      - * To store enviornment keys to connect to db, etc          
                static                      - Web Dev Stuff (HTML, CSS, JS)
                template                    - Web Dev Stuff (HTML, CSS, JS)
        test                                - all of ur testing code is here (unit testing, mocking testing, etc)                                                                           

________________________________________ Spring Decorators/Descriptors Hierarchy ____________________________________________________________________________________________________________                                                                                                                                                                                                                                                                                          */
    //* Program Level:
        @SpringBootApplication                                  // The start of the program -> Spring will manage whatever package this file is in -> Spring will search for beans from this packages 
        @ComponentScan("com.example.ExternalPackage")           // Scan other packaghes - Some files may be outside of the package @SpringBootApplication is in. Add this under @SpringBootApplication to import/scan components of that package!
    
    //* Bean/Class Level:
        @Scope("singleton/prototype/request/session")           // Change the scope of a bean. When i make a @Component -> becomes a singleton bean by default
            @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)     // Do this to repalce @Scope("prototype") etc since doing it like that is bad practice
            @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)   /* PROXY - Say CLASS1 injects CLASS2. CLASS2 is a prototype bean. CLASS1 is the default singleton bean. Whenever CLASS1 calls CLASS2 -> it will go thru a proxy so that a new instance of CLASS2 will be called each time!
                @Component
                @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)     //! BinarySearch was a singleton bean before (get same copy of bean isntance) but now changed it to a prototype bean (get new instance of bean each time u call it)
                public class BinarySearch implements SearchingAlgorithm{...}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  */
     
    //* Bean Creation & Destruction Time (Methods that run at those times)
        @PostConstruct                                          // Do x after bean creation     - "Run this method AFTER the CREATION of the particular bean (and after all dependencies are injected)"
        @PreDestroy                                             // Do x before bean destruction - "Run this method BEFORE the DESTRUCTION of a particular bean (bean removed from application context)"                                                                   

    //* Method Level Annotations 
        @Bean                                                   // In a @Configuration class (used to make dummy data), a method annted by @Bean -> Spring will register a bean whose name will be the method's name and whose type will be the method's return type. ("When a method is annotated by this, JavaConfig  will execute it if it encounters it and register the return value as a bean within a beanfactory")
        @Transient                                              // (See Process Notes in Ambicode notes) Put this on top of a instance variable that u dont want to have a column for in the db. Ex: age cant be calculated from dob, so i cna have age in the Student class, but putting @transient tells the db to not make a column of it
        @Transactional                                          // PUT reqs service - no need to make a JPQL queries (@Query), can use getters and setters to update entities in db!!!!

    //* Component Annotations
        @Component                                              // Generic Component
        @Named                                                  // Does the same thing as @Component but its from the javax.inject API while @Component is from Spring API. Lets you name beans
        @Service                                                // API Layer 2) More Suited for a Business Service (same instance used, Huge memory gains, helps wwith scalability)
        @Controller                                             // API Layer 1) The Controller in a MVC Pattern
        @Repository                                             // API Layer 3) Storage, retrieval, search in a relational db. Spring gives u some methods to use to help with talking to db


    //* Dependency Injection Annotations
        @Autowired                                              // Same as @Inject but this is Spring's own annotation. Better to use @Inject since @Autowired is only for Spring 
            @Inject                                             // Dependency Injection (used to inject a Service into a class's method to instantize it for usage)
        
        @Service                                                // (Used for API Stuff, says that this class has a business logic (some method)) Service Class - Spring will create an object as a "singleton". If you inject this in multiuple classes, it will reuse the same instance! Huge memory gains and good for scaling by spining up multiple instances
        @Component                                              // Turns classes (or methods for API) to a bean so Spring can manage it (Adds it to the application context) - Same as @Service but @Service is more readable (tells Spring that it needs to mamage instances of x class)
        @Primary                                                /* Ex: You have two @Components that implement same interface -> want to @Autowire the inject interface type -> Spring will be confused since there are two @Components of that type-> @Primary sets one of them to be the primary class to be injected for that type  
            - // Sorting Algorithms 
                @Component
                @Primary
                public class BubbleSort implements SortingAlgorithm{...}

                @Component
                public class QuickSort implements SortingAlgorithm{...}
            - // Searching Algorithm 
                public class BinarySearch implements SearchingAlgorithm{
                    @AutoWired                                                       
                    private SortingAlgorithm sortingAlgorithm;                          // defaults to BubbleSort since its set as @Primary
                }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            */
        @Qualifier("DependencyName")                                                                                                                                                                                                                                                                                            /* 
            - // Sorting Algorithms 
                @Component
                @Qualifier("bubble_sort")                                               
                public class BubbleSort implements SortingAlgorithm{...}
            - // Searching Algorithm 
                public class BinarySearch implements SearchingAlgorithm{
                    @AutoWired                                                       
                    @Qualifier("bubble_sort")                                           // Injecting 'bubble_sort' component/dependency                            
                    private SortingAlgorithm sortingAlgorithm;  
                }                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            */
        @Scope                                  // Do you want the injected dependency to be copies or new instances? See Scope notes in 'Bean/Class Scope Level' Section above

    


//? ________________________________________ REST - Decorators/Descriptors ____________________________________________________________________________________________________________                                                                                                                                                                                                                                                                                          */
    //* REST 1 - Mapping Decorators:
        @RestController                                     // put this on top of the main class so it serves as a REST endpoint (will have all the get,post,put, etc requests) (it will also have @SpringBootApplication on top of it)
        @RequestMapping(path = "api/v1/path")               // PATH COLLECTION - api layer (kind of like the route where we use the methods but dont implement them)
        // Mappings:
            @GetMapping                                     // Put on top of a method. Use to map a GET requests onto a specific method.
                @GetMapping("/path2")                       // In a rest controller, put this on top of a method you want activated when u go to this path      
            @PostMapping
            @DeleteMapping
            @PutMapping

    //* REST 2 - Getting Data From Requests
        // a) Get Request Body 
            public void registerStudent(
                @RequestBody Student student){...}                      // Use this next to a parameter of a function where ur getting the object from the client. Ex: register(@RequestBody Form form)

        // b) Get Url/Path Variable
            @SomeMapping(path = "{urlVariable}")                        // Can pass in url variables like this     
            public void deleteStudent(
                @PathVariable("urlVariable") String urlVariable){}      // @PathVariable - get url path
            
        // c) Get Request Parameter Variable:
            public void updateStudent(
                @RequestParam(required = false) String email){...}      // @RequestParam - get url parameters
        
        // d) Get Header:
            public void updateStudentKey(
                @RequestHeader("key") String key){...}                  // @RequestHeader - get headers
            

    //* REST 3 - Connect Model to DATABASE
        @Entity                                             // For Spring Hibernate. To map the model to the database. Add this along with @Table  
        @Table                                              // To map the model clas to a table in the database. Add this alogn with @Entity 
        // Inside model class (put all this inside)
            @Id
            @SequenceGenerator(
                name = "<dbname>_sequence",
                sequenceName = "<dbname>_sequence",
                allocationSize = 1
            )
            @GeneratedValue(
                strategy = GenerationType.SEQUENCE,
                generator = "<dbname>_sequence"
            )  

    //* REST 4 - Data Access               
        @Repository                                         // Means that this class can access the database. No need to implement anything since things are implemented by Spring already   
        @Configuration                                      // used on a class that is used to pollute the db with dummy data using @Bean methods? 
            CommandLineRunner                               // CommandLineRunner - (need @Bean to run) a functional interface that a bean should run when its contained in a SpringApplication. Can run multiple CommandLineRunners
        
        // Queries
            @Query("SELECT s FROM Student s WHERE s.email = ?1")   // JQL not SQL. `Student` is the @Entity we defined. Using s to represent it. Can then access properties like email














//?________________________________________ REST API Decorators/Descriptors Hierarchy ____________________________________________________________________________________________________________                                                                                                                                                                                                                                                                                          */

    //* Program Level:
        @SpringBootApplication 
       
    //* Bean/Class Level:
        @RestController                                     // put this on top of the main class so it serves as a REST endpoint (will have all the get,post,put, etc requests) (it will also have @SpringBootApplication on top of it)

        @Repository                                         // Means that this class can access the database. No need to implement anything since things are implemented by Spring already   
        @Configuration                                      // (Make dummy data) used on a class that is used to pollute the db with dummy data using @Bean methods? 
        @Service
        // Model
            @Entity                                             // For Spring Hibernate. To map the model to the database. Add this along with @Table  
            @Table                                              // To map the model clas to a table in the database. Add this alogn with @Entit
    
            //* Method Level Annotations         
        @RequestMapping(path = "api/v1/path")               // MAPPING PATH - api layer (kind of like the route where we use the methods but dont implement them)\
        @Bean          
        //* Mappings
            @GetMapping                                     // Put on top of a method. Use to map a GET requests onto a specific method.
            @PostMapping
            @DeleteMapping
            @PutMapping
                optional: (path = "{urlVariable}")          // Can pass in url variables like this   

        //* Method Parameters Level
            @RequestBody                                    // Use this next to a parameter of a function where ur getting the object from the client. Ex: register(@RequestBody Form form)
            @PathVariable                                   // @PathVariable - get url path
            @RequestParam                                   // @RequestParam - get url parameters
            @RequestHeader                                  // @RequestHeader - get headers


    //* Instance Variable level
        @Transient 
    