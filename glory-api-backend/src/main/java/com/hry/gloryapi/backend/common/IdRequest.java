package com.hry.gloryapi.backend.common;

import java.io.Serializable;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 只有ID的请求对象
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class IdRequest implements Serializable {

    /**
     * id
     */
    @NotNull(message = "id不能为空")
    private Long id;

    private static final long serialVersionUID = 1L;
}