package com.myme.mywarehome.domains.issue.application.port.out;

import com.myme.mywarehome.domains.issue.application.domain.Issue;

public interface CreateIssuePort {
    Issue create(Issue issue);
}
