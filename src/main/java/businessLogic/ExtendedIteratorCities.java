package businessLogic;

import java.util.List;
import java.util.NoSuchElementException;

public class ExtendedIteratorCities  implements ExtendedIterator<String> {
	private List<String> citiesList;
	private int pos;
	
	public ExtendedIteratorCities(List<String> cities) {
		citiesList = cities;
		pos = 0;
	}

	@Override
	public boolean hasNext() {
		if(pos <= citiesList.size() - 1) return true;
		else return false;
	}

	@Override
	public String next() {
		String ret = citiesList.get(pos);
		this.pos++;
		return ret;
	}

	@Override
	public String previous() {
		String ret = citiesList.get(pos - 1);
		this.pos--;
		return ret;
	}

	@Override
	public boolean hasPrevious() {
		if(pos > 0) return true;
		else return false;
	}

	@Override
	public void goFirst() {
		this.pos = 0;
	}

	@Override
	public void goLast() {
		this.pos = citiesList.size();
	}
}
