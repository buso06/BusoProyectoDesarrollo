package cr.ac.ucr.mediacloud.model;
import java.time.LocalDateTime;
public class Usuario {
 private Integer id; private String nombre; private String correo; private String clave; private Rol rol; private boolean activo; private LocalDateTime fechaCreacion;
 public Integer getId(){return id;} public void setId(Integer id){this.id=id;} public String getNombre(){return nombre;} public void setNombre(String nombre){this.nombre=nombre;} public String getCorreo(){return correo;} public void setCorreo(String correo){this.correo=correo;} public String getClave(){return clave;} public void setClave(String clave){this.clave=clave;} public Rol getRol(){return rol;} public void setRol(Rol rol){this.rol=rol;} public boolean isActivo(){return activo;} public void setActivo(boolean activo){this.activo=activo;} public LocalDateTime getFechaCreacion(){return fechaCreacion;} public void setFechaCreacion(LocalDateTime fechaCreacion){this.fechaCreacion=fechaCreacion;}
 public boolean esAdmin(){return rol==Rol.ADMINISTRADOR;}
}
