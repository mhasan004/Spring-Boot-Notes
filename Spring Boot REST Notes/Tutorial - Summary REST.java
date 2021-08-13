import java.beans.Transient;
/*________________________________________ Repository Commands (Get, Post, Put/Save, Delete) ____________________________________________________________________________________________________________
    RepositoryClassName repository                                                          */
    repository.save(List)                           // Save/Put

    // FIND BY EMAIL:
        // Add to Repository Interface: Adding findStudentByEmail() method -> will return Optional<Student> -> Usage: can use .isPresent() to find if email exists or not
            @Query("SELECT s FROM Student s WHERE s.email = ?1")                            //! This is JQL not SQL. `Student` is the @Entity we defined. Using s to represent it. Can then access properties like email
            Optional<Student> findStudentByEmail(String email);                             // returns Optional<Student> - A container object which may or may not contain a non-null value. Can use .isPresent() to check if it exists or not (nul or not)
        // Usage: 
            Optional<Student> studentOptional = studentRepository.findStudentByEmail(student.getEmail());   // 1) findStudentByEmail() -> returns Optional<Student> -> .isPresent() is true if there is a value
            if (studentOptional.isPresent()){                                                               // 2) see if email exists using .isPresent() -> error if Student exists
                throw new IllegalStateException("Email exists!");
            }
            studentRepository.save(student);                                                /* 3) Save the Student if dont exist           /*


______________________________ SUMMARY (Layers of App)  ____________________________________________________________________________________________________________
    (See Process Notes below) ---- Client <-> API Layer <-> Service Layer <-> Data Access Layer <-> DB  

    __________________________________________ 1/5) APP Layer __________________________________________                                                                                                 */                                                                               
        // [APP] MainPackage/App.java
            @SpringBootApplication                                                              //!                                       
            public class DemoApplication {
                public static void main(String[] args) {
                    SpringApplication.run(DemoApplication.class, args);
                }
            }                                                                                                                                                                   /*
    __________________________________________ 2/5) API Layer (Inject Service) __________________________________________                                                                                         */
        // [MODEL / SCHEMA]......................................................... MainPackage/StudentPackage/Student.java
            // Turn this class into a table in the db using Hibernate 
            @Entity                                                                             //! For hibernate. mapping model to database
            @Table                                                                              //! Mapping this model class to a table in the database
            public class Student {
                @Id                                                                             //! Need to add these inside model
                @SequenceGenerator(
                        name = "student_sequence",
                        sequenceName = "student_sequence",
                        allocationSize = 1
                )
                @GeneratedValue(
                        strategy = GenerationType.SEQUENCE,
                        generator = "student_sequence"
                )

                // Instance variables - Schema of table
                    private long id;                                                            // ID for the table (postgres will auto makew this when i insert)
                    private LocalDate dob;
                    @Transient                                                                  //! telling db to not make a column for age     
                    private int age;                                                            //! age wont be stored in db, when i get a Studnet, all instance variables will be given except this, age will be calculated using getAge() getter methods. 
                // Constructors
                    public Student(long id, String name, String email, LocalDate dob) {...}
                    public Student(String name, String email, LocalDate dob) {...}              // one without id since db auto makes it
                // Getters and Setters                                                  
                    public int getAge() {                                                       //!!!!!!!!!!!!! Getters/Setters -> When i get Students from DB -> Will run these methods to get the instance variabels not on db (@Transient variables)
                        return Period.between(this.dob, LocalDate.now()).getYears();
                    }
            }

        // [CONTROLLER (Inject Services)]............................... MainPackage/StudentPackage/StudentController.java
            @RestController                                                                     //! Indicate this is a REST controller
            @RequestMapping(path = "api/v1/student")                                            //! This is the Route/Path/Endpoint
            public class StudentController {
                // 1) Injecting Service Dependency
                    private final StudentService studentService;                                // 1a) Object/dependency we want to inject to use its methods. Before had to create a new instance (bad) 
                    @Autowired                                                                  // 1b) Injecting `StudentService` object into `StudentController` Constructor so that we will use the same instance of `StudentService` as other classes that use it
                    public StudentController(StudentService studentService) {           
                        this.studentService = studentService;                   
                    }
                // 2) Get Endpoint     
                    @GetMapping                                                                 //! GET requests to this endpoint activates this method
                    public List<Student> getStudents(){
                        return studentService.getStudents();                               
                    }
                // 3) POST Endpoint (add Student to db if the request body's email field doesnt exist in db)     
                    @PostMapping                                                                //! POST requests to this endpoint activates this method
                    public void registerStudent(@RequestBody Student student){                  //! @RequestBody lets us get the POST req body
                        studentService.registerStudent(student);                                // only fields of Student that's passed in will be let through!
                    }
                // 4) Delete Endpoint (get the student id from the url and delete it from db if it exists)
                    @DeleteMapping(path="{studentId}")                                          //! Delete mapping + Passing in url variable
                    public void deleteStudent(@PathVariable("studentId") Long studentId){       //! @PathVariable("studentId"): Pass in url path variable in the paramter.
                        studentService.deleteStudent(studentId);
                    }
            }                                                                                                                               /*
    __________________________________________ 3/5) Service/Business Layer (Inject Repository) __________________________________________                            */
        /* [Dependencies (Service/Components) (Inject Repositories)]............................ MainPackage/StudentPackage/StudentService.java
            - Services in Java but are like Controllers in Express. Implements methods. 
            - Since we can export them out and import into classes. We need to "export" them out as @Services and @Inject them into the Java Controllers (Dependency Injection)*/

            @Service                                                                            //! Set it as a Service Class so Spring can find it for dependency injection?
            public class StudentService {
                // 1) Inject Repository Dependency 
                    private StudentRepository studentRepository;                                // Repository object to intreact with db
                    @Autowired                                                                  // Injecting StudentRepository to this service so that we can acces the db
                    public StudentService(StudentRepository studentRepository){ 
                        this.studentRepository = studentRepository;
                    }
                // 2) Method used in GET endpoint: Get all Students from db and return them as a list    
                    public List<Student> getStudents(){
                        return studentRepository.findAll();                                     // GET REQ - Returns a list of all students in db!
                    }
                // 3) Method used in POST endpoint: See if user with email exists, if not, add them
                    public void registerStudent(Student student){
                        Optional<Student> studentOptional = studentRepository.findStudentByEmail(student.getEmail());   // 1) findStudentByEmail() -> returns Optional<Student> -> .isPresent() is true if there is a value
                        if (studentOptional.isPresent()){                                                               // 2) see if email exists using .isPresent() -> error if Student exists
                            throw new IllegalStateException("Email exists!");
                        }
                        studentRepository.save(student);                                        // 3) Save the Student if dont exist           
                    } 
                // 4) Method used in DELETE endpoint  
                    public void deleteStudent(Long studentId){
                        if (studentRepository.existsById(studentId)){                                                                       
                            studentRepository.deleteById(studentId);
                        } else{
                            throw new IllegalStateException("Student Id "+studentId+" does not exist!");
                        }
                    }
            }                                                                                                                                   /*
            
    __________________________________________ 4/5) Repository/Data Access Layer (custom database methods - most work already done by 1s lien!)  (Access DB) __________________________________________                  */
        // MainPackage/StudentPackage/StudentRepository.java
            @Repository                                                                         //!
            public interface StudentRepository extends JpaRepository<Student, Long>{            // Want to work with the Student model. The id of it is long (`private long id`)   
                
                // FOR POST REQ: Adding a Query to the repository so it can search by email.
                // -> will return Optional<Student> -> Usage:  can use .isPresent() to find if email exists or not
                @Query("SELECT s FROM Student s WHERE s.email = ?1")                            //! This is JQL not SQL. `Student` is the @Entity we defined. Using s to represent it. Can then access properties like email
                Optional<Student> findStudentByEmail(String email);                             //! returns Optional<Student> - A container object which may or may not contain a non-null value. Can use .isPresent() to check if it exists or not (nul or not)
            }                                                                                                                                                   /*

    __________________________________________ 5/5) Configuration (Adding Dummy Data) __________________________________________                  */
        // [Configuration] - Populate the db with dummy data - MainPackage/StudentPackage/StudentConfig.java
            @Configuration                                                                      //! Adding dummy data to the db
            public class StudentConfig {
                @Bean                                                                           //! Need this so that this CommandLineRunner runs (JavaConfig  will execute this method when it encounters it)
                CommandLineRunner commandLineRunner(StudentRepository studentRepository){       //! CommandLineRunner - a functional interface that a bean should run when its contained in a SpringApplication. Can run multiple CommandLineRunners
                    return args -> {                                                            // Lambda expressions for dummy data. (Parameters) -> { Body }
                        Student Tom = new Student(                                                  // Student Tom
                            "Tom", "Tommy@gmail.com", LocalDate.of(2000, Month.JANUARY, 5)
                        );
            
                        Student Alex = new Student(                                                 // Student Alex
                            "Alex", "Alex@gmail.com", LocalDate.of(2001, Month.JANUARY, 18)
                        );
            
                        studentRepository.saveAll(                                              //! Saving the two students to the db using the repository.save(List)
                                List.of(Tom, Alex)
                        );
            
                    };
                }
            }
        


















            

/*
______________________________ PROCESS NOTES ____________________________________________________________________________________________________________
    * Process of Getting/Storing Schema/Model to DB with @Transient variables:
        - Student Model Ex:
            - Student has id, name, and age (@Transient)
            - id and age arnt instantized by the constructor -> only name is stored in the db
        - What happens when i add Student to DB? 
            - id field is auto generated by the db
            - name is stored
            - since age is @Transient, its not added
        - What happens when i get Student from db?
            - id and name are given since only those were stored
            - age is @Transient so Spring uses the getter to get the age
    * Layers:
        API request ->
            Application.java            @SpringBootApplication
                Controller              @RestController, @RequestMapping(path = "api/v1/some_path")
                    Service             @Service
                        Repository      @Repository (interface)
                            DB

*/