/*___________________________ 19) Autowiring LVL2 - Naming Dependency with @Qualifier _______________________________________________________________________________________________________________________________________________________________________________________________________________________________________*/
    @Qualifier("DependencyName")                        // Can put under @Component -> Specify the name of the dependency -> To inject: Type @Qualifier("...") under @Autowired 

    // Sorting Algorithms (Setting Component/Dependency name using @Qualifier)
        @Component
        @Qualifier("bubble_sort")                                           //!
        public class BubbleSort implements SortingAlgorithm{/**/}


    // Searching Algorithm (dependent on SortingAlgorithm)
    public class BinarySearch implements SearchingAlgorithm{
        @AutoWired                                                       
        @Qualifier("bubble_sort")                                           //! Injecting bubble_sort component/dependency                            
        private SortingAlgorithm sortingAlgorithm;  
    }

/*___________________________ 20) Bean Scope lvl 1 (Setting Beans) (@Scope) _______________________________________________________________________________________________________________________________________________________________________________________________________________________________________
    //!------------------------------ Beans Scopes: -------------------------------------------------------------------------------------------------------------
        By default, all beans are singleton beans
        1) singleton Beans: Whenever you get a bean from the Application Context 9same memory loc) -> you get copy/instance of the same bean*/
            SearchingAlgorithm binarySearch  = applicationContext.getBean(BinarySearch.class);
            SearchingAlgorithm binarySearch2 = applicationContext.getBean(BinarySearch.class);
            print(binarySearch, binarySearch2);                                                             /* (pyton print for show) -> will shopw the same loc
        2) prototye - You get a new instance of a bean whenever you request it                                                                                                                                                                                                                                                                                                                                                     */
            @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)                                                 /* Better practive than doing @Scope("prototype")
        3) request - (FOR APIS) One bean is created per HTTP Request (when ur in the scope of the request)                                                                                                                                                                                                                                                      
        4) session - (FOR APIS) One bean is created per HTTP Request (when ur in the scope of the request)                                                                                                                                                                                                                                                                                                                                    */

    //!----------------------------------------------------------------------------------------------------------------------------------------------------------
    //* Change bean scope (When i make a @Component -> becomes a singleton bean by default -> Will chnage to prototye with @Scope*/
        @Component
        @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)                                                     //! BinarySearch was a singleton bean before but now changed it to a prototype bean
        public class BinarySearch implements SearchingAlgorithm{/**/}
        // In Main: 
            SearchingAlgorithm binarySearch  = applicationContext.getBean(BinarySearch.class);
            SearchingAlgorithm binarySearch2 = applicationContext.getBean(BinarySearch.class);
            print(binarySearch, binarySearch2);                                                             // (pyton print for show) -> different location! -> different instances

/*___________________________ 21) Bean Scope lvl 2 (Proxies) (@Scope(value, proxyMode))  _______________________________________________________________________________________________________________________________________________________________________________________________________________________________________
    //! ------------------------------ Beans Scopes + Proxies: ------------------------------------------------------------------------------------------
		Bean Scope Test -
        * LVL 1) Both Default Singleton Bean:
            * (COPIES) personDAO1 and personDAO2 have same id, their respective JDBC connections also have the same id
        * LVL 2) PersonDAO = Prototype Bean, JdbcConnection = Default Singleton Bean
            - PersonDAO1 and PersonDAO2 have different ids!
            - The JDBC connection of both are the same (because we left it as the default)
        * LVL 3) PersonDAO = Default Singleton Bean, JdbcConnection = Prototype Bean
            - Unexpected ->  (COPIES) PersonDAO1 and PersonDAO2 have same id, their respective JDBC connections also have the same id
            - Why? Since PersonDAO gets 1 instance of JdbcConnection -> and we use the same copy of PersonDAO and doesnt realize that JdbcConnection is a prototype bean
            - Solution: PROXY - we want the same copy of PersonDAO but a different jdbc connection each time -> need to use a proxy
        * LVL 4) PersonDAO = Default Singleton Bean. JdbcConnection = Prototype Bean + PROXY
            - Same PersonDAO id, BUT each JdbcConnection is a different id
            - everytime PersonDAO calls/injects JdbcConnection -> it goes thru a proxy so that a new instance of JdbcConnection is made  
            - WHY IMPORTANT: Keeps the number of instances low (same PersonDAO) + increases different connections (JdbcConnection)                                                                                                                                                                                                                                                              */
    //!----------------------------------------------------------------------------------------------------------------------------------------------------------

    @Component
    public class PersonDAO {                                                                            // PersonDAO is a dingleton bean so it will just be copies
        @Autowired      
        JdbcConnection jdbcConnection;                                                                  // JdbcConnection is a prototype bean, + gopes thu a proxy -> so a new instance is called each time (no copies)
    }

    @Component
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)   //! PROXY - Say PersonDAO injects JdbcConnection. JdbcConnection is a prototype bean. Whenever PersonDAO calls JdbcConnection -> it will go thru a proxy so that a new instance of JdbcConnection will be called each time! new connections for each copy of PersonDAO
    public class JdbcConnection {
        public JdbcConnection() {
            System.out.println("I just injected JDBC dependency!");										// Will run each time its injected into a copy of PersonDAO!
        }
    }

    PersonDAO personDAO1 = applicationContext.getBean(PersonDAO.class);
    PersonDAO personDAO2 = applicationContext.getBean(PersonDAO.class);
    System.out.println("personDAO 1:      {}", personDAO1);
    System.out.println("personDAO 2:      {}", personDAO2);
    System.out.println("personDAO 1 JDBC: {}", personDAO1.getJdbcConnection());
    System.out.println("personDAO 2 JDBC: {}", personDAO2.getJdbcConnection());


/*___________________________ 24) Life Cycle of Bean (@PostConstruct, @PreDestory)  _______________________________________________________________________________________________________________________________________________________________________________________________________________________________________
    //! ------------------------------ Life Cycle of Bean (Do x at creation, after creation, before destruction of bean): -----------------------------------------
        - The entire lifecycle of a bean is maintained by Spring IoC Container (Inversion of Control). Will destroy a bean when no longer in use          
        -  Intresting problem - https://stackoverflow.com/questions/38068303/spring-autowiring-order-and-postconstruct                                                                                                                                                                                                                                                                                                                                                                                                                                        */

        @PostConstruct
        void postConstruct(){...}                                                                                                                                                                                                                                                                                                                                                                                                                                    /*
            - "Run this method AFTER the CREATION of the particular bean (and after all dependencies are injected)"
            - As soon as the bean is created -> postConstruct runs                                                                                                                                                                                                                                                                                                                                                                                                    */
        @PreDestroy                             
        void preDestruction(){...}                                                                                                                                                                                                                                                                                                                                                                                                                                    /*
            - "Run this method BEFORE the DESTRUCTION of a particular bean"                                                                                
            - before bean is destroyed ( bean removed from application context) -> preDestruction runs                                                                                                                                                                                                                                                                                                                                                                                                    */

    //!----------------------------------------------------------------------------------------------------------------------------------------------------------
    
    // @PostConstruct - Do x after bean creation
    // @PreDestroy - Do x before bean destruction
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
        





