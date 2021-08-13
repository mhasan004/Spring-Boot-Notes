/* 1) CONNECT TO DB:
        File:src->main->java->resources->application.properties 
            spring.datasource.url=jdbc:postgresql://localhost:5432/path
            spring.datasource.username=<dbusername>                                             
            spring.datasource.password=<dbpassword>
            spring.jpa.hibernate.ddl-auto=create-drop
            spring.jpa.show-sql=true
            spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
            spring.jpa.properties.hibernate.format_sql=true

____________________________ Add to Model _______________________________________________________________*/
    @Entity                                 // For Spring Hibernate. To map the model to the database. Add this along with @Table  
    @Table                                  // To map the model class to a table in the database. Add this alogn with @Entity 
    // Inside model class (put all this inside)
        @Id
        @SequenceGenerator(
            name = "<dbname>_sequence",                         // <dbname> = db name = student                       
            sequenceName = "<dbname>_sequence",
            allocationSize = 1
        )
        @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "<dbname>_sequence"
        )                                                                                                                        /*
______________________________ Steps to Connect to DB _____________________________________________________________

    1) Map Model Class to a Table in Database using Hibernate                                    */
        // [ON MODEL] Student.java
            @Entity                                                         //! For hibernate. mapping model to database
            @Table                                                          //! Mapping this model class to a table in the database
            public class Student {
                @Id                                                         //! Need to ad these inside model
                @SequenceGenerator(
                    name = "student_sequence",
                    sequenceName = "student_sequence",
                    allocationSize = 1
                )
                @GeneratedValue(
                    strategy = GenerationType.SEQUENCE,
                    generator = "student_sequence"
                )

                // Instance variables                   -> schema of table
                // Constructors
                // Getters and Setters
            }                                                                           /*

    2) Connect IntelliJ to the Postgres local db
        1) click the Database tab (all the way on the right side )
        2) + -> New Data Source -> PostgresSQL
        3) Chnage the database name to whatever u had it    (db name is `student` for the baove ex)
            username: postgres (usually)
            password: .3..

        4)  Check to see if intelliJ is connected: 
                student -> public -> table and `student_sequence` is there
            Check to see if the `student` and `student_sequence` tables were added to the db
                SQL Shell:  
                    \c student  
                    \d                  (see the two dbs) 
                    \d student          (see schema of student db, the scema will be the instance vars)
                    
                    
    3) Repository [StudentRepository.java]  (easy)              
        * Will add a reposity class. Nothing to implement! Will let us enteract with database easily! 
        * No need to implement! Easy!  (see JpaRepository methods)
            * Example fucntions we cna use: find, findAll, findById, save, delete, etc!*/
        // Exmaple Repository.java 
            @Repository    
            public interface Repository extends JpaRepository<datatype_of_model, datatype_of_tables_ID> {}
    
        // 1) REPOSITORY................................................................MainPackage/StudentPackage/StudentRepository.java
            @Repository    
            public interface StudentRepository extends JpaRepository<Student, Long>{    // Want to work with the Student model. The id of it is long (`private long id`)
                
            }
    
        // 2) Injecting to StudentService.java......................................MainPackage/StudentPackage/StudentService.java
            @Service                                                                    
            public class StudentService {
                private StudentRepository studentRepository;                            //! instance to use the repository
                @Autowired
                public StudentService(StudentRepository studentRepository){             //! injecting the repository 
                    this.studentRepository = studentRepository;
                }
    
                public List<Student> getStudents(){
                    //return List.of(new Student("Ella"));
                    return studentRepository.findAll();                                 // Now instead of returning dummy data, i can now return all striends that are in the db!                         
                }
            }                                                                                                                           /*
    
    4) Configuration - adding dummy data to db.............................................. MainPackage/StudentPackage/StudentConfig.java                                                                                */
            @Configuration                                                                  //! Adding dummy data to the db
            public class StudentConfig {
                @Bean                                                                       //! Need this so that this CommandLineRunner runs (JavaConfig  will execute this method when it encounters it)
                CommandLineRunner commandLineRunner(StudentRepository studentRepository){   //! CommandLineRunner - a functional interface that a bean should run when its contained in a SpringApplication. Can run multiple CommandLineRunners
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
        

                