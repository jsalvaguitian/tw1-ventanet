package com.tallerwebi.infraestructura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Proveedor;
import com.tallerwebi.dominio.entidades.UsuarioAuth;
import com.tallerwebi.dominio.utils.PasswordUtil;
/*
public class RepositorioUsuarioAuthImpl {

    private Map<Long, UsuarioAuth> usuariosDB;
    private static Long proximoId;


    public RepositorioUsuarioAuthImpl() {
        proximoId = 0L;
        this.usuariosDB = inicializarLaBD();
    }

    private Map<Long, UsuarioAuth> inicializarLaBD() {
        this.usuariosDB = new HashMap<>();

        String contraseniaHasheadaCliente = PasswordUtil.hashear("Batman1!");
        String contraseniaHasheadaProveedor = PasswordUtil.hashear("Elbarto1!");

        this.guardar(new Cliente("bart@gmail.com", contraseniaHasheadaCliente));
        this.guardar(new Proveedor("jesi@gmail.com", contraseniaHasheadaProveedor));

        return this.usuariosDB;
    } 

    public Boolean guardar(UsuarioAuth usuario) {
        usuario.setId(++proximoId);
        this.usuariosDB.put(usuario.getId(), usuario);
        return true;
    }

    public UsuarioAuth buscarPorMail(String emailIngresado) {
        List<UsuarioAuth>usuarios = new ArrayList<>(this.usuariosDB.values());
    
        for(UsuarioAuth unUsuario : usuarios){
            if(unUsuario.getEmail().equals(emailIngresado)){
                return unUsuario;
            }
        }

        return null;
    }

    public Map<Long, UsuarioAuth> getUsuariosDB() {
        return usuariosDB;
    }

    public void setUsuariosDB(Map<Long, UsuarioAuth> usuariosDB) {
        this.usuariosDB = usuariosDB;
    }

    public static Long getProximoId() {
        return proximoId;
    }

    public static void setProximoId(Long proximoId) {
        RepositorioUsuarioAuthImpl.proximoId = proximoId;
    }

    

}
*/