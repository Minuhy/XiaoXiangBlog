package minuhy.xiaoxiang.blog.bean.admin;


/**
 * һ���༭��
 * @author y17mm
 *
 */
public class EditBean {
    boolean canEdit;
    String label;
    String name;
    String hint;
    String value;
    String type; // Ĭ��Ϊtext
    String[] valChoose; // ˫��Ϊֵ������Ϊ��ʾ����Ϊ������

    public EditBean() {
    }

    public EditBean( String label, String name, String hint, Object value) {
        this.canEdit = true;
        this.label = label;
        this.name = name;
        this.hint = hint;
        this.value = String.valueOf(value);
        type = "text";
    }

    public EditBean(boolean canEdit, String label, String name, String hint, String value) {
        this.canEdit = canEdit;
        this.label = label;
        this.name = name;
        this.hint = hint;
        this.value = value;
        type = "text";
    }

    
    
    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String[] getValChoose() {
		return valChoose;
	}

	public void setValChoose(String[] valChoose) {
		this.valChoose = valChoose;
	}

	public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    @Override
    public String toString() {
        return "EditBean{" +
                "canEdit=" + canEdit +
                ", label='" + label + '\'' +
                ", name='" + name + '\'' +
                ", hint='" + hint + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
