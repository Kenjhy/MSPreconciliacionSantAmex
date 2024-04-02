package mx.com.nmp.mspreconciliacion.util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mx.com.nmp.mspreconciliacion.model.dto.CatalogoComisionesDTO;
import mx.com.nmp.mspreconciliacion.model.enums.CorresponsalEnum;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.PagoConciliadoEPA;
import mx.com.nmp.mspreconciliacion.model.preconciliacion.PagoConciliadoMIT;

public class CatalogoComision {

	private static final Logger LOG = LoggerFactory.getLogger(CatalogoComision.class);

	public  void ordenaStringNumericos( List<String> temp) {
		Collections.sort(temp, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return extractInt(o1) - extractInt(o2);
			}

			int extractInt(String s) {
				String num = s.replaceAll("\\D", "");
				// return 0 if no digits found
				return num.isEmpty() ? 0 : Integer.parseInt(num);
			}
		});
	}

	protected static String removeAccent(String input) {
		String output = Normalizer.normalize(input, Normalizer.Form.NFD);
		return output.replaceAll(CODIGO_ACENTOS, "");
	}

	private static final String CODIGO_ACENTOS = "\\p{InCombiningDiacriticalMarks}+";

	private List<String> obtenerSucursal(CorresponsalEnum corresponsal, List<PagoConciliadoMIT> pagoConciliadoMIT, List<PagoConciliadoEPA> pagoConciliadoEPA){
		List<String> sucursalesB = new ArrayList<>();

		if (corresponsal.equals(CorresponsalEnum.SANTANDER)) {
			for (PagoConciliadoMIT pagoMit : pagoConciliadoMIT) {
				if (pagoMit.getCorresponsal().getSucursal()!= null && pagoMit.getCorresponsal().getSucursal().length() >0)
					sucursalesB.add(pagoMit.getCorresponsal().getSucursal());
			}
		}else if (corresponsal.equals(CorresponsalEnum.AMEX)) {
			for (PagoConciliadoEPA pagoEpa : pagoConciliadoEPA) {
				if (pagoEpa.getCorresponsal().getSucursal()!= null  && pagoEpa.getCorresponsal().getSucursal().length()> 0) {
					sucursalesB.add(pagoEpa.getCorresponsal().getSucursal());
				}
			}
		}
		return sucursalesB;
	}


	private List<String> obtenerBanco(CorresponsalEnum corresponsal, List<PagoConciliadoMIT> pagoConciliadoMIT){
		List<String> bancos = new ArrayList<>();
		if (corresponsal.equals(CorresponsalEnum.SANTANDER)) {
			for (PagoConciliadoMIT pagoMit : pagoConciliadoMIT) {
				if (pagoMit.getCorresponsal().getBancoEmisor() != null && pagoMit.getCorresponsal().getBancoEmisor().length() > 0 ) {
					bancos.add(StringUtils.capitalize(pagoMit.getCorresponsal().getBancoEmisor().toLowerCase()));
				}
			}
		}else if (corresponsal.equals(CorresponsalEnum.AMEX)) {
			bancos.add("AMEX");
		}
		return bancos;
	}

	public CatalogoComisionesDTO contruyeCatalogoComision(CorresponsalEnum corresponsal, List<PagoConciliadoMIT> pagoConciliadoMIT, List<PagoConciliadoEPA> pagoConciliadoEPA) {
		CatalogoComisionesDTO catalogoComisionesDTO = new CatalogoComisionesDTO();
		List<String> sucursales = null;
		List<String> bancos = null;
		HashSet<String> hs = new HashSet<>();

		sucursales = obtenerSucursal(corresponsal, pagoConciliadoMIT, pagoConciliadoEPA);
		bancos = obtenerBanco(corresponsal, pagoConciliadoMIT);

		hs.addAll(sucursales);
		sucursales.clear();
		sucursales.addAll(hs);
		ordenaStringNumericos(sucursales);
		LOG.info("value Lista = {}",sucursales);
		Arrays.sort(bancos.toArray());
		bancos = bancos.stream().distinct().sorted().collect(Collectors.toList());

		LOG.info("value Lista = {}",bancos);
		catalogoComisionesDTO.setSucursal(sucursales);
		catalogoComisionesDTO.setBanco(bancos);

		return catalogoComisionesDTO;
	}
}
