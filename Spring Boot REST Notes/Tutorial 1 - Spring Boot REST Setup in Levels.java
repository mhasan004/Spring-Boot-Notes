
//--------------------------------------- LVL 1 - Start ------------------------------------------------------------------------------
    // MainPackage/App.java
        @SpringBootApplication
        @RestController											                // This decorater says: This class will hold all the get, post, put, etc mappings
        public class App {
            public static void main(String[] args) {
                SpringApplication.run(App.class, args);
            }
            @GetMapping
            public List<String> getStudents(){                                  // going to localhost:8080 will print this json list
                return List.of("Tom", "Ella");
            }
        }
/*--------------------------------------- LVL 2 - (API LAYER) With Seperated Routes and controllers ---------------------------------------
    * In this part, we will a Student model, Student routes (Student Controller in java) 
    * App will have nothing. We will move @RestController to Student Controller                                     */
    
    // MainPackage/App.java
        @SpringBootApplication
        public class App {
            public static void main(String[] args) {
                SpringApplication.run(App.class, args);
            }
        }
    // MainPackage/StudentPackage/Student.java
        // Didint code the @Model @Table code for simplicity. See the Summary.java to get the full code 
        public class Student {
            // Instance variables
            // Instance variables - Schema of table
                private long id;                                                    // ID for the table (postgres will auto makew this when i insert)
                @Transient                                                          //! telling db to not make a column for age     
                private int age;                                                    //! age wont be stored in db, when i get a Studnet, all instance variables will be given except this, age will be calculated using getAge() getter methods. 
            // Constructors
                public Student(long id, String name, String email, LocalDate dob) {...}
                public Student(String name, String email, LocalDate dob) {...}      // one without id since db auto makes it
            // Getters and Setters
        }
    // MainPackage/StudentPackage/StudentController.java
        @RestController                                                         // Specifying that this is a controller
        @RequestMapping(path = "api/v1/student")                                // path
        public class StudentController {
            @GetMapping
            public List<Student> getStudents(){                                 // (Dummy data) Just ouputs a list of Students. Ouputs as JSON to screen
                return List.of(new Student("Ella"));
            }
        }

/*--------------------------------------- LVL 3 - (BUSINESS/SERVICE LAYER) ---------------------------------------
    * Here, we will move the implementations from Student Controller into the Student Service. 
    * Basically Controller will use and Service will implement (In express, route will use and controller will implement, so theres that name difference). 
    //! Will have TIGHT COUPLING issues which we will solve in next section (See Dependency Injection.java) */
    
    // MainPackage/StudentPackage/StudentController.java
        @RestController                                   
        @RequestMapping(path = "api/v1/student")
        public class StudentController {
            private StudentService studentService = new StudentService();       //! BAD! This is tight coupling! Will be allocating space on the heap for `StudentService` for each `StudentController` object u make! Need to do dependency injection  (See explanation above)         
            
            public StudentController(StudentService studentService){            //! CANT DO! Error -> can't do this since we wont have an instance of the studentService parameter. Need to do dependency injection (See explanation above)                                      
                this.studentService = studentService;                   
            }
    
            @GetMapping
            public List<Student> getStudents(){
                return studentService.getStudents();                            // We just call the business methods now
            }
        }

    // MainPackage/StudentPackage/StudentService.java
        public class StudentService {
            public List<Student> getStudents(){
                return List.of(new Student("Ella"));
            }
        }
    
    
/*--------------------------------------- LVL 3.1 - Using Dependency Injection to make code be loose coupling ---------------------------------------
    * Fixing the tight coupling issues caused by `new ...` issue in LVL 3 where we want to import an object into a class but we can only do so by allocating a new object.
      Now, we will use Dependency Injection to "import" that copy of the object (get rid of `new ..` so you dont create objects)*/

  
    // MainPackage/StudentPackage/StudentController.java
        @RestController                                   
        @RequestMapping(path = "api/v1/student")
        public class StudentController {
            private final StudentService studentService;                        // 0) Object we want to inject to use its methods. Before had to create a new instance (bad) 
            
            @Autowired  //Issue using @Inject                                   // 1) Injecting `StudentService` object into `StudentController` Constructor so that we will use the same instance of `StudentService` as other classes that use it
            public StudentController(StudentService studentService) {   
                this.studentService = studentService;                   
            }
    
            @GetMapping
            public List<Student> getStudents(){
                return studentService.getStudents();                  
            }
        }

    // MainPackage/StudentPackage/StudentService.java
        @Service                                                                // 2) Set it as a Service Class so Spring can find it for dependency injection?
        public class StudentService {
            public List<Student> getStudents(){
                return List.of(new Student("Ella"));
            }
        }


/*--------------------------------------- LVL 4 - Repository (Simple) (Data Access Layer - Accesses DB)  ---------------------------------------*/ 
    @Repository    
    public interface StudentRepository extends JpaRepository<datatype_of_model, datatype_of_tables_ID> {...}

    // 1) REPOSITORY................................................................MainPackage/StudentPackage/StudentRepository.java
        // Will give us all the methods to interact with db (find, findAll, findById, save, delete, etc!)
        // No need to implement! All aree done! Easy!  (see JpaRepository methods)
        @Repository    
        public interface StudentRepository extends JpaRepository<Student, Long>{    // Want to work with the Student model. The id of it is long (`private long id`)
            
        }

    // 2) Injecting to StudentService.java......................................MainPackage/StudentPackage/StudentService.java
        @Service                                                                    
        public class StudentService {
            private StudentRepository studentRepository;                            //! instance to use the repository
            @Autowired
            public StudentService(StudentRepository studentRepository){             //! injectign the repository 
                this.studentRepository = studentRepository;
            }

            public List<Student> getStudents(){
                //return List.of(new Student("Ella"));
                return studentRepository.findAll();                                 // Now instead of returning dummy data, i can now return all striends that are in the db!                         
            }
        }

    
/*--------------------------------------- LVL 4.1 - Configuration (addign dummy data to the DB) ---------------------------------------*/ 
    // MainPackage/StudentPackage/StudentConfig.java
        @Configuration                                                                  //! Adding dummy data to the db
        public class StudentConfig {
            @Bean                                                                       //! Need this so that this CommandLineRunner runs (JavaConfig  will execute this method when it encounters it)
            CommandLineRunner commandLineRunner(StudentRepository studentRepository){
                return args -> {                                                        // Lambda expressions for dummy data. (Parameters) -> { Body }
                    Student Tom = new Student(                                                  // Student Tom
                        "Tom", "Tommy@gmail.com", LocalDate.of(2000, Month.JANUARY, 5)
                    );
        
                    Student Alex = new Student(                                                 // Student Alex
                        "Alex", "Alex@gmail.com", LocalDate.of(2001, Month.JANUARY, 18)
                    );
        
                    studentRepository.saveAll(                                          //! Saving the two students to the db using the repository.save(List)
                            List.of(Tom, Alex)
                    );
        
                };
            }
        }

/*--------------------------------------- LVL 5.1 - CRUD 1 - POST Route (Business logic) ---------------------------------------*/ 
    // Query Ex: Find Student with Email -> Will use see if a Student exists -> if not, can registewr Student
        // 1) POST REQ (Controller) - StudentController.java 
                @RestController                                                        
                @RequestMapping(path = "api/v1/student")
                public class StudentController {
                    // Injecting Serivice Dependency & Constructor...
                    // GET Endpoint: call Services's getStudents() method 
            
                    // POST Endpoint: Get POST form data -> See if user with email exists, if not, add them
                    @PostMapping                                                        // POST requests to this endpoint activates this method
                    public void registerStudent(@RequestBody Student student){          //! @RequestBody lets us get the POST req body (only fields of Student that's passed in will be let through!)
                        studentService.registerStudent(student);
                    }
                }

        // 2) POST REQ (Repository) - Adding a Query to the repository so it can search by email.
            // findStudentByEmail() will return Optional<Student> -> can use isPresent() to find if email exists or not
            @Repository
            public interface StudentRepository extends JpaRepository<Student, Long> {   // Want to work with the Student model. The id of it is long (`private long id`)
                @Query("SELECT s FROM Student s WHERE s.email = ?1")                    //! This is JQL not SQL. `Student` is the @Entity we defined. Using s to represent it. Can then access properties like email
                Optional<Student> findStudentByEmail(String email);                     //! returns Optional<Student> - A container object which may or may not contain a non-null value. Can use .isPresent() to check if it exists or not (nul or not)
            }

        // 3) POST REQ (Service) - StudentService.java 
            @Service
            public class StudentService {
                // Injecting Repository Dependency & Constructor...
                // GET Endpoint METHOD
                
                // POST Endpoint METHOD: See if user with email exists, if not, add them
                public void registerStudent(Student student){
                        Optional<Student> studentOptional = studentRepository.findStudentByEmail(student.getEmail());   // 1) findStudentByEmail() -> returns Optional<Student> -> .isPresent() is true if there is a value
                        if (studentOptional.isPresent()){                                                               // 2) see if email exists using .isPresent() -> error if Student exists
                            throw new IllegalStateException("Email exists!");
                        }
                    studentRepository.save(student);                                                                    // 3) Save the Student if dont exist           
                }
            }
        
/*--------------------------------------- LVL 5.2 - CRUD 2 - DELETE Route (Business logic) ---------------------------------------*/ 

    // 1) DELETE REQ (Controller) - StudentController.java 
        @RestController                                                       
        @RequestMapping(path = "api/v1/student")
        public class StudentController {
            // Injecting Serivice Dependency & Constructor...
            // GET Endpoint: call Services's getStudents() method 
            // POST Endpoint: Get POST form data -> See if user with email exists, if not, add them

            // DELETE Endpoint:
            @DeleteMapping(path="{studentId}")                                          //! Delete mapping + Passing in url variable
            public void deleteStudent(@PathVariable("studentId") Long studentId){       //! @PathVariable("studentId"): Pass in url path variable in the paramter.
                studentService.deleteStudent(studentId);
            }
        
    // 3) DELETE REQ (Service) - StudentService.java 
        @Service
        public class StudentService {
            // Injecting Repository Dependency & Constructor...
            // GET Endpoint METHOD
            // POST Endpoint METHOD: See if user with email exists, if not, add them

            // DELETE Endpoint:
            public void deleteStudent(Long studentId){
                if (studentRepository.existsById(studentId)){
                    studentRepository.deleteById(studentId);
                }
                else{
                    throw new IllegalStateException("Student ID "+studentId+" does not exist!");
                }
            }
            
        }


/*--------------------------------------- LVL 5.3 - CRUD 3 - PUT Route (Business logic) ---------------------------------------*/ 
        @Transactional                  // no need for JPQL queries, cna use getters and setters to update entities in db!!!!
        public void updateStyudent(){...}

















































