package dev.vality.exporter.businessmetrics.metrics.providerterminal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProviderTerminalDto {

    private String providerName;
    private String terminalName;

}
