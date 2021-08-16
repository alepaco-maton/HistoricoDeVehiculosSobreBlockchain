package bo.com.micrium.modulobase;
 
import inv.stamina.modulobase.model.Bitacora;
import inv.stamina.modulobase.model.Label;
import inv.stamina.modulobase.repository.IBitacoraRepository;
import inv.stamina.modulobase.repository.ILabelRepository;
import inv.stamina.modulobase.repository.IUserRepository;
import inv.stamina.modulobase.resources.LabelController;
import inv.stamina.modulobase.resources.dto.LabelRequest;
import inv.stamina.modulobase.resources.dto.LabelResponse;
import inv.stamina.modulobase.security.jwt.JwtTokenUtil;
import inv.stamina.modulobase.service.BitacoraService;
import inv.stamina.modulobase.validator.ApiException;
import inv.stamina.modulobase.validator.LabelValidator;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert; 
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity; 
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

@SpringBootTest
@SpringBootConfiguration 
public class LabelTests {

    @Mock
    IBitacoraRepository bitacoraRepository;

    @Mock
    ILabelRepository labelRepository;

    @Mock
    IUserRepository userRepository;

    @Mock
    HttpServletRequest httpServletRequest;
 
    JwtTokenUtil jwtTokenUtil;

    BitacoraService bitacoraService;

    LabelController controller;
    LabelValidator validator;

    public LabelTests() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void update() {
        try {
            jwtTokenUtil = new JwtTokenUtil();
            jwtTokenUtil.setSecret("alepacomatonalepacomatonalepacomatonalepacomatonalepacomatonalepacomatonalepacomatonalepacomatonalepacomatonalepacomatonalepacomatonalepacomatonalepacomaton");

            String userName = "admin";
            String roleName = "Administracion";
            String ipAddress = "192.168.0.15";
            int id = 1;

            Label model = new Label(id, "key", "value", "group.key", true);
//            String token = "eyJhbGciOiJIUzUxMiJ9.eyJyb2wiOiJBZG1pbmlzdHJhY2lvbiIsInN1YiI6ImFkbWluIiwiaWF0IjoxNjI5MDc0MTQ1LCJleHAiOjE2MjkxMTczNDV9.q0TBgzOxZZHt7Uof8vtxDL1QpSmwiPPC4TaDTaNwsX3-Zlg7Wy9aWm9xLmob2DKpcNhzZ8SjrkQ_FMZ8HUTC0Q";
            String token = "Bearer " + jwtTokenUtil.generateToken(userName, roleName);
            when(httpServletRequest.getHeader(JwtTokenUtil.KEY_TOKEN)).thenReturn(token);
            when(httpServletRequest.getRemoteAddr()).thenReturn(ipAddress);
            when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("htttp://test.unit.bo/proyecto/v1/label/1"));
            when(httpServletRequest.getMethod()).thenReturn("PUT");
            when(labelRepository.findById(id)).thenReturn(Optional.of(model));
            when(labelRepository.save(model)).thenReturn(model);
//            when(jwtTokenUtil.getUsernameFromToken(token)).thenReturn(userName);
            when(bitacoraRepository.save(new Bitacora())).thenReturn(new Bitacora());
//            when(httpServletRequest.getParameter("ln")).thenReturn("Kashyap");

            bitacoraService = new BitacoraService();
            bitacoraService.setJwtTokenUtil(jwtTokenUtil);
            bitacoraService.setRespository(bitacoraRepository);

            validator = new LabelValidator();
            
            controller = new LabelController();
            controller.setBitacoraService(bitacoraService);
            controller.setJwtTokenUtil(jwtTokenUtil);
            controller.setRepository(labelRepository);
            controller.setUserRepository(userRepository);
            controller.setValidator(validator);
            controller.setHttpServletRequest(httpServletRequest);

            LabelRequest request = new LabelRequest(model.getKey(), "new value", model.getGroupLevel());
            BindingResult result = new BeanPropertyBindingResult(request, "request");

            ResponseEntity<LabelResponse> response = controller.update(token, ipAddress, "Etiqueta - Actualizar", request, id, result);

            Assert.assertEquals(response.getStatusCodeValue(), 200);
        } catch (ApiException ex) {
            ex.printStackTrace();
        }

    }

}
