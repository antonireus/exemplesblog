package org.sample.fileServer;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class EncoderUtilsTest {

    @Test
    public void testCaractersNormals() {
        
        String valor = "!#$&+-.^_`|~09AZaz";    
        assertEquals(valor, EncoderUtils.headerEncode(valor, StandardCharsets.UTF_8));
    }

    // exemple de https://tools.ietf.org/html/rfc5987#section-3.2.2
    @Test
    public void testPoundIso88591() {
        String valor = "\u00A3 rates";            
        assertEquals("%a3%20rates", EncoderUtils.headerEncode(valor, StandardCharsets.ISO_8859_1));

    }
    
    // exemple de https://tools.ietf.org/html/rfc5987#section-3.2.2
    @Test
    public void testPoundEuroUTF8() {
        String valor = "\u00A3 and € rates";    
        assertEquals("%c2%a3%20and%20%e2%82%ac%20rates", EncoderUtils.headerEncode(valor, StandardCharsets.UTF_8));
    }    
    
}
