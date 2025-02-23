FROM fluent/fluentd:v1.16-1

# Use root account to use apt
USER root

# Install plugins
RUN apk add --no-cache --update \
    ruby \
    ruby-dev \
    gcc \
    make \
    libc-dev && \
    gem install fluent-plugin-elasticsearch -v 5.3.0 && \
    apk del build-base ruby-dev && \
    rm -rf /tmp/* /var/tmp/* /usr/lib/ruby/gems/*/cache/*.gem

USER fluent