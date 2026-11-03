package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.repository.CarroRepository;
import br.org.edu.ifrn.LojaCarro.CarroException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CarroServiceTest {

    @Mock
    private CarroRepository carroRepository;

    @InjectMocks
    private CarroService carroService;

    private Carro carro;
    private Carro carroMercedes;
    private Carro carroPreco0;

    @BeforeEach
    void setUp() {
        carro = new Carro();
        carroMercedes = new Carro();
        carroPreco0 = new Carro();
        carro.setAno(2000);
        carro.setId(1L);
        carro.setModelo("BMW");
        carro.setPreco(20000);

        carroMercedes.setAno(2025);
        carroMercedes.setId(2L);
        carroMercedes.setModelo("Mercedes");
        carroMercedes.setPreco(20000);

        carroPreco0.setAno(2004);
        carroPreco0.setId(3L);
        carroPreco0.setModelo("VW");
        carroPreco0.setPreco(0);
    }

    @Test
    void save() {
        when(carroRepository.save(any(Carro.class))).thenReturn(carro);

        Carro resultado = carroService.save(carro);

        assertNotNull(resultado);
        assertEquals(carro.getId(), resultado.getId());
        assertEquals("BMW", resultado.getModelo());
        verify(carroRepository, times(1)).save(carro);
    }

    @Test
    void findAll() {
        when(carroRepository.findAll()).thenReturn(List.of(carro, carroPreco0));

        List<Carro> carros = carroService.findAll();

        assertEquals(2, carros.size());
        verify(carroRepository).findAll();
    }

    @Test
    void testaValidacoesParaSalvarCarro() {
        Carro carroModeloNulo = new Carro();
        carroModeloNulo.setPreco(10000);
        assertThrows(CarroException.class, () -> carroService.save(carroModeloNulo));

        Carro carroModeloVazio = new Carro();
        carroModeloVazio.setModelo("   ");
        carroModeloVazio.setPreco(10000);
        assertThrows(CarroException.class, () -> carroService.save(carroModeloVazio));

        assertThrows(CarroException.class, () -> carroService.save(carroMercedes));

        Carro carroPrecoNegativo = new Carro();
        carroPrecoNegativo.setModelo("Audi");
        carroPrecoNegativo.setPreco(-500);
        assertThrows(CarroException.class, () -> carroService.save(carroPrecoNegativo));

        verify(carroRepository, never()).save(any(Carro.class));
    }
}