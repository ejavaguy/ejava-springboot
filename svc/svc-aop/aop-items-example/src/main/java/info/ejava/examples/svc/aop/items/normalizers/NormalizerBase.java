package info.ejava.examples.svc.aop.items.normalizers;

public class NormalizerBase {

    /**
     * This method formats a string such that the leading character of each 
     * work is in a capital letter followed by lower-case letters.
     * @param name
     */
    public String normalizeName(String name) {
        if (name==null) { return null; }
        
        StringBuilder normalizedString = new StringBuilder();
        for (String tok: name.split("\\s")) {
            if (normalizedString.length() > 0) {
                normalizedString.append(" ");
            }
            normalizedString.append(tok.substring(0, 1).toUpperCase());
            if (tok.length() > 1) {
                normalizedString.append(toLower(tok.substring(1)));
            }
        }
        return normalizedString.toString();
    }
    
    
    public String toUpper(String value) {
        if (value==null) { return null; }
        return value.toUpperCase().trim();
    }
    
    public String toLower(String value) {
        if (value==null) { return null; }
        return value.toLowerCase().trim();
    }
}
