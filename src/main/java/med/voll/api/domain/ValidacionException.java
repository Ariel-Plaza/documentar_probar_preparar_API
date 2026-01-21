package med.voll.api.domain;

//extiende de runtime porque es mas especifo
public class ValidacionException extends RuntimeException {
//   Recibe como parametro el mensaje
    public ValidacionException(String mensaje) {
//        envie el mensaje a la clase de la cual hereda
        super(mensaje);
    }
}