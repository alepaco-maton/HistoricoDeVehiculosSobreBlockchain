/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.service;

import inv.stamina.modulobase.model.Parameter;
import inv.stamina.modulobase.commons.ParametroID;
import inv.stamina.modulobase.commons.ParameterType;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import inv.stamina.modulobase.repository.IParameterRepository;

/**
 *
 * @author alepaco.maton
 */
@Log4j2
@Service
@Component
@Order(2)
@Scope("singleton")
public class ParameterService {

    @Autowired
    InicializationService inicializationService;

    @Autowired
    private IParameterRepository repository;

    private HashMap<Integer, Parameter> listParameters;

    private final SimpleDateFormat sdf = new SimpleDateFormat(ParameterType.FORMATO_FECHA_HORA);

    @PostConstruct
    public void init() {
        log.info("ParametroService inicializado");
    }

    public synchronized Parameter getParametro(Integer idParametro) {
        return cargarParametros().get(idParametro);
    }

    public synchronized Object getParamVal(Integer idParametro) {
        Parameter p = cargarParametros().get(idParametro);

        switch (p.getType()) {
            case ParameterType.TIPO_CADENA:
                return p.getValue();

            case ParameterType.TIPO_COLOR:
                return p.getValue();

            case ParameterType.TIPO_FECHA:
                try {
                    return sdf.parse(p.getValue());
                } catch (Exception e) {
                    log.error("Erro de parse fecha, " + e.getMessage(), e);
                    return null;
                }
            case ParameterType.TIPO_NUMERICO:
                return new BigDecimal(p.getValue());
            case ParameterType.TIPO_BOOLEANO:
                return Boolean.parseBoolean(p.getValue());
            case ParameterType.TIPO_LISTADO_VALORES_NUMERICOS:
                return p.getValue();
            case ParameterType.TIPO_LISTADO_VALORES_TEXTO:
                return p.getValue();
        }

        return null;
    }

    /**
     * Este metodo debe ser invocado cuando se haga alguna modificacion a un
     * Parameter para que el cambio se manifieste en el resto del sistema. Si en
     * el transcurso del Desarrollo se crean terceras clases que son de tipo
     * singleton estas clases deberan proverer mecanismos para reinicar sus
     * atributos propios para que desde aqui sean invocados y asi el cambio del
     * Parameter sean aplicables en todo contexto.
     *
     *
     */
    public synchronized void restartParameter() {
        log.info("****** Reiniciarparametros..");
        listParameters = null;

        try {
            Parameter reaload = repository.findById(ParametroID.RELOAD_PARAMETER_ID).get();
            reaload.setValue("true");
            repository.save(reaload);
        } catch (Exception e) {
            log.error("Error al cargar ó guardar el parametro RELOAD con ID=" + ParametroID.RELOAD_PARAMETER_ID, e);
        }
    }

    private synchronized HashMap<Integer, Parameter> cargarParametros() {
        boolean reload = false;
        if (listParameters == null) {
            reload = true;
        } else {
            reload = verifRealoadParameters();
        }

        if (reload) {
            log.info("\n\n ***Recargando los parametros***");
            listParameters = new HashMap<Integer, Parameter>();
            for (Parameter item : repository.findAll()) {
                listParameters.put(item.getId(), item);
                log.info(item);
            }

            log.info("*** Final de cargar parametros ***\n\n ");
            updateParameterReload();

        }
        return listParameters;
    }

    private boolean verifRealoadParameters() {
        Optional<Parameter> p = repository.findById(ParametroID.RELOAD_PARAMETER_ID);

        if (p.isPresent()) {
            return p.get().getValue().equals("1");
        }

        return false;
    }

    private boolean updateParameterReload() {
        Optional<Parameter> p = repository.findById(ParametroID.RELOAD_PARAMETER_ID);

        if (p.isPresent()) {
            p.get().setValue("0");
            repository.save(p.get());

            log.info("Valor del parametro reaload  actualizado a false");

            return true;
        }

        return false;
    }

}
