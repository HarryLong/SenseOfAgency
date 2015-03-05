package panels;

public interface ListPanelListener<T> {
	public void newClicked(int id);
	public void deleteClicked(int id);
	public void selectedElement(int id, T element);
}
