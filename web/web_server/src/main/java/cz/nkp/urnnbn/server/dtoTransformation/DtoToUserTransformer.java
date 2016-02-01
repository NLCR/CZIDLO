package cz.nkp.urnnbn.server.dtoTransformation;

import java.security.NoSuchAlgorithmException;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO.ROLE;
import cz.nkp.urnnbn.utils.CryptoUtils;

public class DtoToUserTransformer {

    private final UserDTO dto;

    public DtoToUserTransformer(UserDTO dto) {
        this.dto = dto;
    }

    public User transform() throws NoSuchAlgorithmException {
        User result = new User();
        result.setEmail(dto.getEmail());
        result.setId(dto.getId());
        result.setLogin(dto.getLogin());
        if (dto.getPassword() != null) {
            String salt = CryptoUtils.generateSalt();
            result.setPasswordSalt(salt);
            result.setPasswordHash(CryptoUtils.createSha256Hash(dto.getPassword(), salt));
        }
        result.setAdmin(dto.getRole() == ROLE.SUPER_ADMIN);
        return result;
    }
}
