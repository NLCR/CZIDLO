package cz.nkp.urnnbn.server.dtoTransformation;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO.ROLE;

public class UserDtoTransformer extends DtoTransformer {

    private final User original;

    public UserDtoTransformer(User original) {
        this.original = original;
    }

    @Override
    public UserDTO transform() {
        UserDTO result = new UserDTO();
        result.setId(original.getId());
        result.setLogin(original.getLogin());
        result.setCreated(dateTimeToStringOrNull(original.getCreated()));
        result.setModified(dateTimeToStringOrNull(original.getModified()));
        result.setEmail(original.getEmail());
        if (original.isAdmin()) {
            result.setRole(ROLE.SUPER_ADMIN);
        } else {
            result.setRole(ROLE.ADMIN);
        }
        return result;
    }
}
