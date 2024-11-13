package com.ssg.wannavapibackend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserGradeLog is a Querydsl query type for UserGradeLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserGradeLog extends EntityPathBase<UserGradeLog> {

    private static final long serialVersionUID = -1096025599L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserGradeLog userGradeLog = new QUserGradeLog("userGradeLog");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final EnumPath<com.ssg.wannavapibackend.common.Grade> grade = createEnum("grade", com.ssg.wannavapibackend.common.Grade.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updateAt = createDateTime("updateAt", java.time.LocalDateTime.class);

    public final QUser user;

    public QUserGradeLog(String variable) {
        this(UserGradeLog.class, forVariable(variable), INITS);
    }

    public QUserGradeLog(Path<? extends UserGradeLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserGradeLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserGradeLog(PathMetadata metadata, PathInits inits) {
        this(UserGradeLog.class, metadata, inits);
    }

    public QUserGradeLog(Class<? extends UserGradeLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

