package br.com.debra.nfe;

import br.com.debra.nfe.wsdl.NFeRecepcaoEvento.NFeRecepcaoEvento4Stub;
import br.com.debra.nfe.wsdl.NFeStatusServico4.NFeStatusServico4Stub;

public interface NfeTest {

	static void setUpBeforeClass() throws Exception {}
	
    default void testeStatusServico(NFeStatusServico4Stub stub) throws Exception {}
    
    default void testeCancelamento(NFeRecepcaoEvento4Stub stub) throws Exception {}
    
}
