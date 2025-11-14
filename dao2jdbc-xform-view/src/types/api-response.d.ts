type ApiResponse<T> = {
  data: T | null;
  error: SystemError|null;
  success: boolean;
};

type SystemError = {
    timeStamp: string;
    statusCode: string;
    message: string;
}