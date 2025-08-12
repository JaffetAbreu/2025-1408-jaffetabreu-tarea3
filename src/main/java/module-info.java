module com.programacion.tarea3alfonso {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.programacion.tarea3alfonso to javafx.fxml;
    exports com.programacion.tarea3alfonso;
    exports com.tuempresa.conversor;
    opens com.tuempresa.conversor to javafx.fxml;
}