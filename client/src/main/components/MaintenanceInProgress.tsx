import { ToolOutlined } from "@ant-design/icons";
import { Result } from "antd";

export const MaintenanceInProgress = () => {
  return (
    <Result
      title="Maintenance in progress. Come back later."
      icon={<ToolOutlined />}
    />
  );
};
