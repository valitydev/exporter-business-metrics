# exporter-business-metrics

daway -> exporter-business-metrics -> prometheus

сервис экспортирует в прометеус метрики с количеством платежей/выплат которые сгруппированы по фиксированному количеству
лейблов,
типа shop, terminal, provider, country, bank, currency, status, собирает он их `select * from dw` с базы daway

это делается в рамках проекта по алертингу для бизнеса для отправки уведомлений по событиям процессинга типа конверсии
по провайдерам\терминалам итд
