/*___________________________ LVL 2 - Beans + Dependency Injection (Using Spring to Manage Beans) (@Component, @Autowired)_______________________________________________________________________________________________________________________________________________________________________________________________________________________________________
    //!------------------------------ Need to tell Spring 3 things: ------------------------------------------------------------------------------------------
        1) What are the different beans that Spring has to manage?                                                                                                                                                                                                                                  */
        @Component                                              /* Adding this on top of a class ->tells Spring to manage the class -> bean     
        2) What are the dependencies for a bean? ex: need to tell Spring that the dependency for `BinarySearch` is `SortingAlgorithm` )                                                                                                                                                                         */
            @Autowired                                              /* Dependency Injection -> says that this class (BinarySearch) depends on `SortingAlgorithm`
        3) Where should Spring search fot the Bean?
            Whatever package `@SpringBootApplication` is in -> Spring boot will scan that package so no need to do this step!
    
        * Getting a Bean (Spring is managing all the beans (@Components, etc), can now get them with ApplicationContext):                                                                                                                                                                                                                */
            ApplicationContext applicationContext = SpringApplication.run(MainAppClass.class, args);        // applicationContext - lets us access the ApplicationContext to be able to import beans
            SortingAlgorithm sortingAlgorithm = applicationContext.getBean(SortingAlgorithm.class);		    // getting a bean
    //!----------------------------------------------------------------------------------------------------------------------------------------------------------

/*___________________________ 5) What's Happening in the Background _______________________________________________________________
    //!------------------------------ Whats Happening in the Background (Hows Spring managing beans) ------------------------------------------------------------------------------------------
       Log all the things Spring do: application.properties  -> logging.level.org.springframework = debug    

       1) Spring goes through the package (where @SpringBootApplication is in) and finds all @Components, @Services, etc classes
       2) Once Spring finds all the stuff that it needs to manmage 
           -> will start creating the bean instances for each class and will try to identify the dependencies (constructor injection vs setter injection)
    //!----------------------------------------------------------------------------------------------------------------------------------------------------------*/

/*___________________________ 7) Three Types of Autowiring/Dependency Injection (Constructor vs Setter Injection vs nothing) _______________________________________________________________________________________________________________________________________________________________________________________________________________________________________*/
    //!------------------------------ Three Types of Dependency Injection ------------------------------------------------------------------------------------------
        // 1) Dont need to user Setter or Constructor (Also called setter injection)
            // Ex: Say `SortingAlgorithm` interface is implemented by two classe (both @Components)
            // AutoWiring by @Primary       - Two @Components implements SortingAlgorithm -> but one is @Primary
                @AutoWired                                                                    
                private SortingAlgorithm sortingAlgorithm;                          //* (Best way) Dynamic Injection - defaults to whatever class is set to @Primary for this datatype
            // AutoWiring by Name           - Two @Components implements SortingAlgorithm -> but none has @Primary 
                @AutoWired                                                          //* (2nd Best way) Pusing @Qualifier() to pick the dependency/component i want to inject
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
        3) Setter Injection - lets you set algorithm if u want
            @AutoWired                                                                    
            private SortingAlgorithm sortingAlgorithm;
            public void setSortingAlgorithm(SortingAlgorithm sortingAlgorithm){                        
                this.sortingAlgorithm = sortingAlgorithm;
            }                                                                                                                                                                                                                                                                                                 */
    //!----------------------------------------------------------------------------------------------------------------------------------------------------------*/

/*___________________________ 20) Bean Scope lvl 1 (Setting Beans) (@Scope) _______________________________________________________________________________________________________________________________________________________________________________________________________________________________________
    //!------------------------------ Beans Scopes LVL 1: Setting Scope - @Scope("...") ------------------------------------------------------------------------------------------
        - Bean Scopes: By default, all beans are singleton beans
            1) Singleton Beans: Whenever you get a bean from the Application Context 9same memory loc) -> you get copy/instance of the same bean*/
                SearchingAlgorithm binarySearch  = applicationContext.getBean(BinarySearch.class);
                SearchingAlgorithm binarySearch2 = applicationContext.getBean(BinarySearch.class);
                print(binarySearch, binarySearch2);                                                             /* (pyton print for show) -> will shopw the same loc
            2) prototye - You get a new instance of a bean whenever you request it                                                                                                                                                                                                                              */
                @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)                                                 /* Better practive than doing @Scope("prototype")
            3) Request - (FOR APIS) One bean is created per HTTP Request (when ur in the scope of the request)
            4) Session - (FOR APIS) One bean is created per HTTP Request (when ur in the scope of the request)       
        
        - Change bean scope (When i make a @Component -> becomes a singleton bean by default -> Will chnage to prototye with @Scope*/
            @Component
            @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)                                                        //! BinarySearch was a singleton bean before but now changed it to a prototype bean
            public class BinarySearch implements SearchingAlgorithm{...}
    //!----------------------------------------------------------------------------------------------------------------------------------------------------------

/*___________________________ 21) Bean Scope lvl 2 (Proxies) (@Scope(value, proxyMode))  _______________________________________________________________________________________________________________________________________________________________________________________________________________________________________
    //! ------------------------------ Beans Scopes Lvl 2: Proxies - @Scope(value, proxyMode): ------------------------------------------------------------------------------------------
        Bean Scope Test -
        * LVL 1) Both the class and its dependency are Singleton Beans (default):
            * (ARE COPIES) personDAO1 and personDAO2 have same id, their respective JDBC connections also have the same id
        * LVL 2) PersonDAO = Prototype Bean, JdbcConnection = Default Singleton Bean
            - PersonDAO1 and PersonDAO2 have different ids!
            - The JDBC connection of both are the same (because we left it as the default)
        * LVL 3) PersonDAO = Default Singleton Bean, JdbcConnection = Prototype Bean
            - Unexpected ->  (ARE COPIES) PersonDAO1 and PersonDAO2 have same id, their respective JDBC connections also have the same id
            - Why? Since PersonDAO gets 1 instance of JdbcConnection -> and we use the same copy of PersonDAO and doesnt realize that JdbcConnection is a prototype bean
            - Solution: PROXY - we want the same copy of PersonDAO but a different jdbc connection each time -> need to use a proxy
        * LVL 4) PersonDAO = Default Singleton Bean. JdbcConnection = Prototype Bean + PROXY
            - Same PersonDAO id, BUT each JdbcConnection is a different id!
            - everytime PersonDAO calls/injects JdbcConnection -> it goes thru a proxy so that a new instance of JdbcConnection is made  
            - WHY IMPORTANT: Keeps the number of instances low (same PersonDAO) + increases different connections (JdbcConnection)                                                                                                                                                                                                   */

            @Component
            public class PersonDAO {
                @Autowired
                JdbcConnection jdbcConnection;                                                                  //! a proxy gets injected now. whenever i call this, a new JdbcConnection is injected by the proxy!        
            }

            @Component
            @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)   //! PROXY - Say CLASS1 injects CLASS2. CLASS 2 is a prototype bean. Whenever CLASS1 calls CLASS2 -> it will go thru a proxy so that a new instance of CLASS2 will be called each time!
            public class JdbcConnection {
                public JdbcConnection() {
                    System.out.println("I just injected JDBC dependency!");										// Will run each time its injected!
                }
            }

            PersonDAO personDAO1 = applicationContext.getBean(PersonDAO.class);
            PersonDAO personDAO2 = applicationContext.getBean(PersonDAO.class);
            System.out.println("personDAO1:      {}", personDAO1);                                              // ex: id 1 (same id as personDAO2)
            System.out.println("personDAO2:      {}", personDAO2);                                              // ex: id 1 (same id as personDAO1)
            System.out.println("personDAO1 JDBC: {}", personDAO1.getJdbcConnection());                          // ex: id 200 (The injected JDBCs have different ids)
            System.out.println("personDAO2 JDBC: {}", personDAO2.getJdbcConnection());                          // ex: id 300 (The injected JDBCs have different ids)
    //!----------------------------------------------------------------------------------------------------------------------------------------------------------

/*___________________________ 24) Life Cycle of Bean (@PostConstruct, @PreDestory)  _______________________________________________________________________________________________________________________________________________________________________________________________________________________________________
    //! ------------------------------ Life Cycle of Bean (Do x at creation, after creation, before destruction of bean): -----------------------------------------                                                                                                                               
        - The entire lifecycle of a bean is maintained by Spring IoC Container (Inversion of Control). Will destroy a bean when no longer in use          
        - Intresting problem - https://stackoverflow.com/questions/38068303/spring-autowiring-order-and-postconstruct                                                                                                                                                                                  */
            @PostConstruct                                                                                                                               
            void postConstruct(){...}                                                                                                                                                                                                                                                                                                              /*
                - Run this method AFTER the CREATION of the particular bean (and after all dependencies are injected)
                - As soon as the bean is created -> postConstruct runs                                                                                                                                                                                                                                                                             */
            @PreDestroy                             
            void preDestruction(){...}                                                                                                                                                                                                                                                                                                             /*
                - Run this method BEFORE the DESTRUCTION of a particular bean                                                                   
                - before bean is destroyed ( bean removed from application context) -> preDestruction runs                                                                                                                                                                                                                                                                             */
        
        @Component
        public void someThing(){
            @Autowired
            SomeDependency = SomeDependency
            public void someMethod(){}

            @PostConstruct
            void postConstruct(){...}                                       // Will run this method AFTER the bean is created and dependencies injected

            @PreDestroy                             
            void preDestruction(){...}                                      // Will run this just before bean is destroyed (removed from application context)
        }
    //!----------------------------------------------------------------------------------------------------------------------------------------------------------