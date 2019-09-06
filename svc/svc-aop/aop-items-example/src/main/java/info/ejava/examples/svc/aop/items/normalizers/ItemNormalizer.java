package info.ejava.examples.svc.aop.items.normalizers;

import info.ejava.examples.svc.aop.items.dto.ItemDTO;

public class ItemNormalizer<T extends ItemDTO> extends NormalizerBase {
    public T normalize(T item) {
        if (item==null) { return null; }
        item.setName(normalizeName(item.getName()));
        return item;
    }
}
