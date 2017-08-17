package com.ptoceti.osgi.obix.impl.resources.server;

import java.util.List;

import com.google.inject.Inject;
import com.ptoceti.osgi.obix.custom.contract.SearchOut;
import com.ptoceti.osgi.obix.custom.contract.DigitPoint;
import com.ptoceti.osgi.obix.custom.contract.MeasurePoint;
import com.ptoceti.osgi.obix.custom.contract.ReferencePoint;
import com.ptoceti.osgi.obix.custom.contract.SwitchPoint;
import com.ptoceti.osgi.obix.domain.DomainException;
import com.ptoceti.osgi.obix.domain.ObjDomain;
import com.ptoceti.osgi.obix.object.Obj;
import com.ptoceti.osgi.obix.object.Ref;
import com.ptoceti.osgi.obix.resources.ResourceException;
import com.ptoceti.osgi.obix.resources.SearchResource;

public class SearchServerResource extends AbstractServerResource implements SearchResource {

private ObjDomain objDomain;
	
	@Inject
	public SearchServerResource(ObjDomain pointDomain) {
		this.objDomain = pointDomain;
	}
	
	@Override
	public SearchOut search(Ref query) throws ResourceException {
		
		SearchOut result = new SearchOut();
		
		try {
			//Add all the points we could find.
			List<Obj> searchPointList = objDomain.getObixObjByDisplayName(query.getDisplayName());
			for(Obj searchPoint : searchPointList ){
				if( searchPoint.containsContract(MeasurePoint.contract)
						|| searchPoint.containsContract(SwitchPoint.contract)
						|| searchPoint.containsContract(ReferencePoint.contract)
						|| searchPoint.containsContract(DigitPoint.contract)) {
					Ref ref = new Ref();
					ref.setHref(searchPoint.getHref());
					ref.setIs(searchPoint.getIs());
					ref.setDisplayName(searchPoint.getDisplayName());
					ref.setDisplay(searchPoint.getDisplay());
					result.addValue(ref);
				}
			}
			
		} catch( DomainException ex) {
			throw new ResourceException("Exception in " + this.getClass().getName() + ".retrieve", ex);
		}
		
		return result;
	}

}
