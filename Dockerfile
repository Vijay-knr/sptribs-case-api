ARG APP_INSIGHTS_AGENT_VERSION=3.2.6
# Application image

FROM hmctspublic.azurecr.io/base/java:17-distroless

COPY build/libs/sptribs-case-api.jar /opt/app/
COPY lib/applicationinsights.json /opt/app/

EXPOSE 4013
CMD [ "sptribs-case-api.jar" ]
